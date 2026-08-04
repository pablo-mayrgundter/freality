/**
 * Tweet Archive Search — UI layer.
 *
 * A dependency-free, fully client-side search engine for your own X/Twitter
 * archive. X's own search silently drops old tweets; your downloaded archive
 * ("Download an archive of your data") contains all of them in data/tweets.js.
 * This tool parses that file, indexes it, persists it in IndexedDB, and lets
 * you full-text search it. Nothing is ever uploaded.
 *
 * Import formats accepted:
 *   - the whole twitter-YYYY-MM-DD-*.zip           (unzipped in-browser)
 *   - data/tweets.js (and optional account.js)      (dropped directly)
 *   - a plain JSON array of tweets from scrape.js
 *
 * Pure parsing/index/search logic lives in lib.js.
 */

import {
  readZip, parseYTD, parseHandle, normalize, dedupe,
  buildIndex, searchCore, parseQuery,
} from './lib.js';

const $ = (sel) => document.querySelector(sel);

const el = {
  import:   $('#import'),
  drop:     $('#drop'),
  file:     $('#file'),
  search:   $('#search'),
  q:        $('#q'),
  hideRT:   $('#hideRT'),
  hideReply:$('#hideReply'),
  sort:     $('#sort'),
  stats:    $('#stats'),
  reset:    $('#reset'),
  results:  $('#results'),
};

let TWEETS = [];        // normalized tweet records
let INDEX = new Map();  // token -> Set(tweetIndex)
let HANDLE = localStorage.getItem('x.handle') || '';


//////////////////// Persistence (IndexedDB) ////////////////////

const DB_NAME = 'tweet-archive';
const STORE = 'kv';

function openDB() {
  return new Promise((resolve, reject) => {
    const req = indexedDB.open(DB_NAME, 1);
    req.onupgradeneeded = () => req.result.createObjectStore(STORE);
    req.onsuccess = () => resolve(req.result);
    req.onerror = () => reject(req.error);
  });
}

async function dbGet(key) {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const r = db.transaction(STORE).objectStore(STORE).get(key);
    r.onsuccess = () => resolve(r.result);
    r.onerror = () => reject(r.error);
  });
}

async function dbSet(key, val) {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE, 'readwrite');
    tx.objectStore(STORE).put(val, key);
    tx.oncomplete = () => resolve();
    tx.onerror = () => reject(tx.error);
  });
}

async function dbClear() {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE, 'readwrite');
    tx.objectStore(STORE).clear();
    tx.oncomplete = () => resolve();
    tx.onerror = () => reject(tx.error);
  });
}


//////////////////// Search + render ////////////////////

const MAX_RENDER = 500;

function esc(s) {
  return s.replace(/[&<>"]/g, (c) => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
}

function highlight(text, q) {
  const { terms, phrases } = parseQuery(q);
  const parts = [...phrases, ...terms].filter(Boolean)
    .map((t) => t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'))
    .sort((a, b) => b.length - a.length);
  let html = esc(text);
  if (parts.length) {
    html = html.replace(new RegExp('(' + parts.join('|') + ')', 'gi'), '<mark>$1</mark>');
  }
  return html;
}

function fmtDate(ms) {
  return ms ? new Date(ms).toISOString().slice(0, 10) : '';
}

function tweetUrl(tw) {
  if (!tw.id) return '#';
  return HANDLE ? `https://x.com/${HANDLE}/status/${tw.id}` : `https://x.com/i/status/${tw.id}`;
}

function render(hits, q) {
  el.stats.textContent =
    `${hits.length.toLocaleString()} / ${TWEETS.length.toLocaleString()} tweets`;

  if (!hits.length) {
    el.results.innerHTML = '<p class="empty">No matches.</p>';
    return;
  }
  const html = hits.slice(0, MAX_RENDER).map((tw) => {
    const badges = [];
    if (tw.rt) badges.push('<span class="badge">RT</span>');
    if (tw.reply) badges.push('<span class="badge">reply</span>');
    return `<article class="tweet">
      <div class="text">${highlight(tw.text, q)}</div>
      <div class="meta">
        <a href="${tweetUrl(tw)}" target="_blank" rel="noopener">${fmtDate(tw.created) || 'open'}</a>
        <span>&#9829; ${tw.likes.toLocaleString()}</span>
        <span>&#128257; ${tw.rts.toLocaleString()}</span>
        ${badges.join(' ')}
      </div>
    </article>`;
  }).join('');

  const more = hits.length > MAX_RENDER
    ? `<p class="more">showing first ${MAX_RENDER.toLocaleString()} of ${hits.length.toLocaleString()} — narrow your search to see the rest</p>`
    : '';
  el.results.innerHTML = html + more;
}

function currentOpts() {
  return { hideRT: el.hideRT.checked, hideReply: el.hideReply.checked, sort: el.sort.value };
}

let searchTimer = 0;
function runSearch() {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    const q = el.q.value.trim();
    render(searchCore(TWEETS, INDEX, q, currentOpts()), q);
  }, 60);
}


//////////////////// Import flow ////////////////////

function looksLikeTweets(name) {
  return /(^|\/)(tweets?(-part\d+)?\.js|tweet\.js)$/i.test(name);
}
function looksLikeAccount(name) {
  return /(^|\/)account\.js$/i.test(name);
}

async function importFiles(files) {
  showLoading('Reading archive…');
  try {
    let raw = [];
    let handle = '';

    for (const f of files) {
      const lower = f.name.toLowerCase();
      if (lower.endsWith('.zip')) {
        showLoading('Unzipping (large archives take a few seconds)…');
        const entries = await readZip(await f.arrayBuffer(),
          (n) => looksLikeTweets(n) || looksLikeAccount(n));
        const dec = new TextDecoder();
        for (const [name, bytes] of entries) {
          const text = dec.decode(bytes);
          if (looksLikeAccount(name)) handle = handle || parseHandle(text);
          else raw.push(...parseYTD(text));
        }
      } else {
        const text = await f.text();
        if (looksLikeAccount(lower)) handle = handle || parseHandle(text);
        else raw.push(...parseYTD(text));
      }
    }

    if (!raw.length) {
      throw new Error('No tweets found. Drop the ZIP, or the data/tweets.js file from inside it.');
    }

    showLoading(`Indexing ${raw.length.toLocaleString()} tweets…`);
    const tweets = dedupe(raw.map(normalize));
    if (handle) { HANDLE = handle; localStorage.setItem('x.handle', handle); }

    await dbSet('tweets', tweets);
    await dbSet('meta', { handle: HANDLE, count: tweets.length });

    activate(tweets);
  } catch (e) {
    el.import.classList.remove('hidden');
    el.results.innerHTML = `<p class="empty err">${esc(e.message || String(e))}</p>`;
  }
}

function activate(tweets) {
  TWEETS = tweets;
  INDEX = buildIndex(tweets);
  el.import.classList.add('hidden');
  el.search.classList.remove('hidden');

  if (!HANDLE && !localStorage.getItem('x.handleAsked')) promptHandle();
  el.q.focus();
  render(searchCore(TWEETS, INDEX, '', currentOpts()), '');
}

// If we never learned the handle (tweets.js dropped without account.js),
// ask once so tweet links resolve to the right profile.
function promptHandle() {
  localStorage.setItem('x.handleAsked', '1');
  const h = prompt('What is your @handle? (used to build links to each tweet; leave blank to skip)');
  if (h) {
    HANDLE = h.replace(/^@/, '').trim();
    localStorage.setItem('x.handle', HANDLE);
    dbGet('meta').then((m) => dbSet('meta', { ...(m || {}), handle: HANDLE }));
  }
}

function showLoading(msg) {
  el.import.classList.add('hidden');
  el.results.innerHTML = `<p class="loading">${esc(msg)}</p>`;
}


//////////////////// Wiring ////////////////////

function wire() {
  el.file.addEventListener('change', (e) => importFiles([...e.target.files]));

  ['dragenter', 'dragover'].forEach((ev) =>
    el.drop.addEventListener(ev, (e) => { e.preventDefault(); el.drop.classList.add('over'); }));
  ['dragleave', 'drop'].forEach((ev) =>
    el.drop.addEventListener(ev, (e) => { e.preventDefault(); el.drop.classList.remove('over'); }));
  el.drop.addEventListener('drop', (e) => {
    const files = [...(e.dataTransfer.files || [])];
    if (files.length) importFiles(files);
  });

  el.q.addEventListener('input', runSearch);
  el.hideRT.addEventListener('change', runSearch);
  el.hideReply.addEventListener('change', runSearch);
  el.sort.addEventListener('change', runSearch);

  el.reset.addEventListener('click', async () => {
    if (!confirm('Forget this archive from the browser?')) return;
    await dbClear();
    localStorage.removeItem('x.handle');
    localStorage.removeItem('x.handleAsked');
    location.reload();
  });
}

async function boot() {
  wire();
  try {
    const tweets = await dbGet('tweets');
    if (tweets && tweets.length) { activate(tweets); return; }
  } catch { /* fall through to import panel */ }
}

boot();
