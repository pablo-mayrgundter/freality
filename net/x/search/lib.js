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
//
// Reads by slicing the Blob (EOCD tail -> central directory -> just the wanted
// local entries), so a multi-GB archive-with-media is never loaded into memory:
// we only touch the few MB we actually need.
export async function readZip(blob, want) {
  const size = blob.size;
  const dec = new TextDecoder();
  const slice = async (start, end) => new DataView(await blob.slice(start, end).arrayBuffer());

  // Locate End Of Central Directory record: it lies within the last
  // 22 + 65535 bytes (fixed record + max comment).
  const tailLen = Math.min(size, 22 + 0xffff);
  const tail = await slice(size - tailLen, size);
  let eo = -1;
  for (let i = tail.byteLength - 22; i >= 0; i--) {
    if (tail.getUint32(i, true) === 0x06054b50) { eo = i; break; }
  }
  if (eo < 0) throw new Error('Not a ZIP file (no end-of-central-directory record).');

  const count  = tail.getUint16(eo + 10, true);
  const cdSize = tail.getUint32(eo + 12, true);
  const cdOff  = tail.getUint32(eo + 16, true);
  if (cdOff === 0xffffffff || count === 0xffff) {
    throw new Error('This archive is 4GB+ (ZIP64), which this reader does not support. '
      + 'Extract data/tweets.js from the ZIP and drop just that file instead.');
  }

  // Read the whole central directory (small: one record per file, no data).
  const cdBuf = await blob.slice(cdOff, cdOff + cdSize).arrayBuffer();
  const cd = new DataView(cdBuf);
  const cdu8 = new Uint8Array(cdBuf);
  if (cd.byteLength < 4 || cd.getUint32(0, true) !== 0x02014b50) {
    throw new Error('Could not read this ZIP. Extract data/tweets.js and drop just that file.');
  }

  const wanted = [];
  let p = 0;
  for (let i = 0; i < count && p + 46 <= cd.byteLength; i++) {
    if (cd.getUint32(p, true) !== 0x02014b50) break;
    const method   = cd.getUint16(p + 10, true);
    const compSize = cd.getUint32(p + 20, true);
    const nameLen  = cd.getUint16(p + 28, true);
    const extraLen = cd.getUint16(p + 30, true);
    const cmtLen   = cd.getUint16(p + 32, true);
    const localOff = cd.getUint32(p + 42, true);
    const name     = dec.decode(cdu8.subarray(p + 46, p + 46 + nameLen));
    p += 46 + nameLen + extraLen + cmtLen;
    if (want(name)) wanted.push({ name, method, compSize, localOff });
  }

  const out = new Map();
  for (const e of wanted) {
    // The local header's own name/extra lengths tell us where data starts
    // (they can differ from the central directory's).
    const lh = await slice(e.localOff, e.localOff + 30);
    const dataStart = e.localOff + 30 + lh.getUint16(26, true) + lh.getUint16(28, true);
    const raw = new Uint8Array(await blob.slice(dataStart, dataStart + e.compSize).arrayBuffer());
    out.set(e.name, e.method === 0 ? raw : await inflateRaw(raw));
  }
  return out;
}


//////////////////// Parsing ////////////////////

// Archive files look like:  window.YTD.tweets.part0 = [ {...}, ... ]
// Strip the JS assignment and parse the JSON array. Also tolerates a bare
// JSON array (e.g. output of scrape.js).
export function parseYTD(text) {
  const t = text.trim();
  const json = (t[0] === '[') ? t : t.slice(t.indexOf('=') + 1);
  return JSON.parse(json.trim().replace(/;$/, '')); // tolerate a trailing ';'
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
