// Node test for lib.js pure logic. Run: node lib.test.mjs
import zlib from 'node:zlib';
import {
  parseYTD, normalize, parseHandle, dedupe,
  buildIndex, searchCore, tokenize, readZip,
} from './lib.js';

let pass = 0, fail = 0;
const ok = (cond, msg) => { if (cond) { pass++; } else { fail++; console.error('FAIL:', msg); } };
const eq = (a, b, msg) => ok(JSON.stringify(a) === JSON.stringify(b), `${msg} (got ${JSON.stringify(a)})`);

// --- sample archive content ---
const tweetsJs = `window.YTD.tweets.part0 = ` + JSON.stringify([
  { tweet: { id_str: '1', created_at: 'Tue Mar 13 05:12:20 +0000 2018',
             full_text: 'The climate crisis is real &amp; urgent https://t.co/abc',
             entities: { urls: [{ url: 'https://t.co/abc', expanded_url: 'https://example.com/climate' }] },
             favorite_count: 42, retweet_count: 5 } },
  { tweet: { id_str: '2', created_at: 'Wed Jan 01 00:00:00 +0000 2020',
             full_text: 'RT @someone: climate change denial is bad', favorite_count: 0 } },
  { tweet: { id_str: '3', created_at: 'Thu Jun 06 12:00:00 +0000 2019',
             full_text: 'just had lunch', in_reply_to_screen_name: 'friend', favorite_count: 3 } },
  { tweet: { id_str: '4', created_at: 'Fri Feb 02 09:00:00 +0000 2021',
             full_text: 'thinking about #climate policy and carbon taxes', favorite_count: 100 } },
]);

const accountJs = `window.YTD.account.part0 = ` + JSON.stringify([
  { account: { username: 'pmayrgundter', accountId: '999' } },
]);

// --- parse ---
const raw = parseYTD(tweetsJs);
eq(raw.length, 4, 'parseYTD count');
eq(parseHandle(accountJs), 'pmayrgundter', 'parseHandle');

const tweets = dedupe(raw.map(normalize));
eq(tweets.length, 4, 'normalize+dedupe count');
eq(tweets[0].text, 'The climate crisis is real & urgent https://example.com/climate', 'entity decode + url expand');
ok(tweets[0].created > 0, 'created parsed');
eq(tweets[1].rt, true, 'RT detected');
eq(tweets[2].reply, true, 'reply detected');
eq(tweets[0].likes, 42, 'likes');

// dedupe on repeated id
eq(dedupe([{id:'x'},{id:'x'},{id:'y'}]).length, 2, 'dedupe by id');

// --- tokenize ---
eq(tokenize('#climate @bob Carbon-Taxes!'), ['#climate', '@bob', 'carbon', 'taxes'], 'tokenize keeps #@');

// --- index + search ---
const idx = buildIndex(tweets);

// single term "climate" should match tweets 1,2,4 (ids '1','2','4')
let r = searchCore(tweets, idx, 'climate', {});
eq(r.map(t => t.id).sort(), ['1', '2', '4'], 'search climate');

// hide retweets removes id 2
r = searchCore(tweets, idx, 'climate', { hideRT: true });
eq(r.map(t => t.id).sort(), ['1', '4'], 'search climate hideRT');

// AND: "climate policy" only tweet 4
r = searchCore(tweets, idx, 'climate policy', {});
eq(r.map(t => t.id), ['4'], 'AND climate policy');

// phrase: "carbon taxes" only tweet 4; "taxes carbon" (out of order) none
eq(searchCore(tweets, idx, '"carbon taxes"', {}).map(t => t.id), ['4'], 'phrase match');
eq(searchCore(tweets, idx, '"taxes carbon"', {}).length, 0, 'phrase order matters');

// empty query returns all, hideReply drops id 3
eq(searchCore(tweets, idx, '', {}).length, 4, 'empty = all');
eq(searchCore(tweets, idx, '', { hideReply: true }).map(t => t.id).sort(), ['1','2','4'], 'hideReply');

// missing term -> no results
eq(searchCore(tweets, idx, 'zzzznope', {}).length, 0, 'unknown term');

// sort newest / likes
eq(searchCore(tweets, idx, '', { sort: 'newest' })[0].id, '4', 'sort newest');
eq(searchCore(tweets, idx, '', { sort: 'oldest' })[0].id, '1', 'sort oldest');
eq(searchCore(tweets, idx, '', { sort: 'likes' })[0].id, '4', 'sort likes');

// --- ZIP round trip (stored + deflated entries) ---
function makeZip(entries) {
  // entries: [{name, data:Buffer, deflate:bool}]
  const locals = [];
  const centrals = [];
  let offset = 0;
  for (const e of entries) {
    const nameBuf = Buffer.from(e.name);
    const comp = e.deflate ? zlib.deflateRawSync(e.data) : e.data;
    const method = e.deflate ? 8 : 0;

    const lh = Buffer.alloc(30);
    lh.writeUInt32LE(0x04034b50, 0);
    lh.writeUInt16LE(method, 8);
    lh.writeUInt32LE(0, 14);              // crc (ignored by our reader)
    lh.writeUInt32LE(comp.length, 18);    // comp size
    lh.writeUInt32LE(e.data.length, 22);  // uncomp size
    lh.writeUInt16LE(nameBuf.length, 26);
    lh.writeUInt16LE(0, 28);              // extra len
    const localRec = Buffer.concat([lh, nameBuf, comp]);

    const ch = Buffer.alloc(46);
    ch.writeUInt32LE(0x02014b50, 0);
    ch.writeUInt16LE(method, 10);
    ch.writeUInt32LE(0, 16);
    ch.writeUInt32LE(comp.length, 20);
    ch.writeUInt32LE(e.data.length, 24);
    ch.writeUInt16LE(nameBuf.length, 28);
    ch.writeUInt32LE(offset, 42);         // local header offset
    centrals.push(Buffer.concat([ch, nameBuf]));

    locals.push(localRec);
    offset += localRec.length;
  }
  const cdStart = offset;
  const cd = Buffer.concat(centrals);
  const eocd = Buffer.alloc(22);
  eocd.writeUInt32LE(0x06054b50, 0);
  eocd.writeUInt16LE(entries.length, 8);
  eocd.writeUInt16LE(entries.length, 10);
  eocd.writeUInt32LE(cd.length, 12);
  eocd.writeUInt32LE(cdStart, 16);
  return Buffer.concat([...locals, cd, eocd]);
}

const zipBuf = makeZip([
  { name: 'data/tweets.js', data: Buffer.from(tweetsJs), deflate: true },
  { name: 'data/account.js', data: Buffer.from(accountJs), deflate: false },
  { name: 'data/other.js', data: Buffer.from('ignore me'), deflate: true },
]);

const ab = zipBuf.buffer.slice(zipBuf.byteOffset, zipBuf.byteOffset + zipBuf.byteLength);
const want = (n) => /tweets?\.js$/.test(n) || /account\.js$/.test(n);
const entries = await readZip(ab, want);
const dec = new TextDecoder();
ok(entries.has('data/tweets.js'), 'zip has tweets.js');
ok(entries.has('data/account.js'), 'zip has account.js');
ok(!entries.has('data/other.js'), 'zip skipped unwanted');
eq(parseYTD(dec.decode(entries.get('data/tweets.js'))).length, 4, 'zip deflate roundtrip');
eq(parseHandle(dec.decode(entries.get('data/account.js'))), 'pmayrgundter', 'zip stored roundtrip');

console.log(`\n${pass} passed, ${fail} failed`);
process.exit(fail ? 1 : 0);
