/**
 * Pure, browser-and-Node-testable core: archive parsing, ZIP reading,
 * normalization, and search indexing. No DOM, no storage. See app.js for the UI.
 */

//////////////////// ZIP reading (no dependencies) ////////////////////

// DEFLATE via the platform-native DecompressionStream (browser and Node >=18).
export async function inflateRaw(bytes) {
  const stream = new Blob([bytes]).stream().pipeThrough(new DecompressionStream('deflate-raw'));
  return new Uint8Array(await new Response(stream).arrayBuffer());
}

// Minimal reader for standard (non-zip64) ZIP archives. Only entries whose
// name passes `want` are decompressed. Returns Map(filename -> Uint8Array).
export async function readZip(buf, want) {
  const dv = new DataView(buf);
  const u8 = new Uint8Array(buf);
  const n = buf.byteLength;

  // Locate End Of Central Directory record (scan back; comment <= 64KB).
  let eocd = -1;
  for (let i = n - 22; i >= Math.max(0, n - 22 - 65536); i--) {
    if (dv.getUint32(i, true) === 0x06054b50) { eocd = i; break; }
  }
  if (eocd < 0) throw new Error('Not a ZIP file (no end-of-central-directory record).');

  const count = dv.getUint16(eocd + 10, true);
  let p = dv.getUint32(eocd + 16, true); // central directory offset

  const out = new Map();
  const dec = new TextDecoder();
  for (let i = 0; i < count; i++) {
    if (dv.getUint32(p, true) !== 0x02014b50) break;
    const method   = dv.getUint16(p + 10, true);
    const compSize = dv.getUint32(p + 20, true);
    const nameLen  = dv.getUint16(p + 28, true);
    const extraLen = dv.getUint16(p + 30, true);
    const cmtLen   = dv.getUint16(p + 32, true);
    const localOff = dv.getUint32(p + 42, true);
    const name     = dec.decode(u8.subarray(p + 46, p + 46 + nameLen));
    p += 46 + nameLen + extraLen + cmtLen;

    if (!want(name)) continue;

    // Local header tells us where the data actually starts.
    const lNameLen  = dv.getUint16(localOff + 26, true);
    const lExtraLen = dv.getUint16(localOff + 28, true);
    const dataStart = localOff + 30 + lNameLen + lExtraLen;
    const raw = u8.subarray(dataStart, dataStart + compSize);
    const bytes = method === 0 ? raw : await inflateRaw(raw);
    out.set(name, bytes);
  }
  return out;
}


//////////////////// Parsing ////////////////////

// Archive files look like:  window.YTD.tweets.part0 = [ {...}, ... ]
// Strip the JS assignment and parse the JSON array. Also tolerates a bare
// JSON array (e.g. output of scrape.js).
export function parseYTD(text) {
  const t = text.trimStart();
  const json = (t[0] === '[') ? t : t.slice(t.indexOf('=') + 1);
  return JSON.parse(json);
}

export function decodeEntities(s) {
  return s.replace(/&amp;/g, '&').replace(/&lt;/g, '<')
          .replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#39;/g, "'");
}

// Turn an archive tweet record into our compact normalized shape.
export function normalize(rec) {
  const t = rec.tweet || rec; // archive wraps each in {tweet:{...}}
  let text = decodeEntities(t.full_text || t.text || '');

  // Replace t.co links with readable display urls where the archive gives them.
  const urls = (t.entities && t.entities.urls) || [];
  for (const u of urls) {
    if (u.url && u.expanded_url) text = text.split(u.url).join(u.expanded_url);
  }

  return {
    id: t.id_str || t.id || '',
    text,
    created: Date.parse(t.created_at) || 0,
    rt: /^RT @/.test(text) || !!t.retweeted_status,
    reply: !!(t.in_reply_to_status_id_str || t.in_reply_to_screen_name),
    likes: +(t.favorite_count || 0),
    rts: +(t.retweet_count || 0),
  };
}

// account.js:  window.YTD.account.part0 = [ { account: { username: "..." } } ]
export function parseHandle(text) {
  try {
    const arr = parseYTD(text);
    const a = arr[0] && (arr[0].account || arr[0]);
    return (a && a.username) || '';
  } catch { return ''; }
}

export function dedupe(tweets) {
  const seen = new Set();
  const out = [];
  for (const t of tweets) { if (t.id && !seen.has(t.id)) { seen.add(t.id); out.push(t); } }
  return out;
}


//////////////////// Search index ////////////////////

// Keep @mentions and #hashtags whole; otherwise split on non-word chars.
export function tokenize(s) {
  return (s.toLowerCase().match(/[@#]?[a-z0-9_]+/g) || []);
}

export function buildIndex(tweets) {
  const idx = new Map();
  const add = (tok, i) => {
    let set = idx.get(tok);
    if (!set) idx.set(tok, set = new Set());
    set.add(i);
  };
  tweets.forEach((tw, i) => {
    for (const tok of new Set(tokenize(tw.text))) {
      add(tok, i);
      // Index #tag / @name under the bare word too, so a plain "climate"
      // query still matches "#climate". The prefixed form stays searchable.
      if (tok[0] === '#' || tok[0] === '@') add(tok.slice(1), i);
    }
  });
  return idx;
}

// Split a query into required terms and quoted phrases.
export function parseQuery(q) {
  const phrases = [];
  q = q.replace(/"([^"]+)"/g, (_, p) => { phrases.push(p.toLowerCase().trim()); return ' '; });
  return { terms: tokenize(q), phrases };
}

function score(tw, terms, rawLower) {
  const low = tw.text.toLowerCase();
  let s = 0;
  for (const t of terms) {
    let idx = 0;
    while ((idx = low.indexOf(t, idx)) !== -1) { s++; idx += t.length; }
  }
  if (rawLower && low.includes(rawLower)) s += 3; // whole query as a phrase
  return s;
}

/**
 * Core search over a tweet array + prebuilt index.
 * opts: { hideRT, hideReply, sort: relevance|newest|oldest|likes }
 * Returns an array of matching tweet records.
 */
export function searchCore(tweets, index, q, opts = {}) {
  const { terms, phrases } = parseQuery(q);
  let candidates;

  if (terms.length === 0 && phrases.length === 0) {
    candidates = tweets.map((_, i) => i);
  } else {
    const sets = terms.map((t) => index.get(t) || new Set());
    if (terms.length && sets.some((s) => s.size === 0)) {
      candidates = [];
    } else {
      sets.sort((a, b) => a.size - b.size);
      let acc = sets.length ? [...sets[0]] : tweets.map((_, i) => i);
      for (let k = 1; k < sets.length; k++) acc = acc.filter((i) => sets[k].has(i));
      candidates = acc;
    }
    if (phrases.length) {
      candidates = candidates.filter((i) => {
        const low = tweets[i].text.toLowerCase();
        return phrases.every((p) => low.includes(p));
      });
    }
  }

  let hits = candidates.filter((i) => {
    const tw = tweets[i];
    if (opts.hideRT && tw.rt) return false;
    if (opts.hideReply && tw.reply) return false;
    return true;
  });

  const q2 = q.toLowerCase();
  const sort = opts.sort || 'relevance';
  hits.sort((a, b) => {
    const A = tweets[a], B = tweets[b];
    if (sort === 'newest') return B.created - A.created;
    if (sort === 'oldest') return A.created - B.created;
    if (sort === 'likes')  return B.likes - A.likes;
    return score(B, terms, q2) - score(A, terms, q2) || B.created - A.created;
  });
  return hits.map((i) => tweets[i]);
}
