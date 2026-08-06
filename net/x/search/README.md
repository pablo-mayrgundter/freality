# Tweet Archive Search

Search your own X/Twitter tweets — including the old ones X's own search
silently stops returning. A static, single-page app: it runs entirely in your
browser, stores your data locally (IndexedDB), and uploads nothing.

**Live:** https://pablo-mayrgundter.github.io/freality/net/x/search/

## Why not just crawl X?

A static page on GitHub Pages can't crawl X live:

- **CORS** — x.com sends no cross-origin headers, so a browser `fetch()` from
  another domain is blocked.
- **API** — X's v2 free tier has no tweet-search/read access anymore, and paid
  tiers need secrets you can't safely ship in a public page.

So instead this uses the source that *is* complete and needs no server, no keys,
and no rate limits: **your data archive**.

## Usage

1. On x.com: **Settings → Your account → Download an archive of your data**.
   Request it, then wait for the "your archive is ready" email (minutes to ~24h).
2. Open the app and drop the `twitter-YYYY-MM-DD-….zip` onto it.
   (You can also drop just `data/tweets.js` — and `account.js` for tweet links —
   extracted from inside the ZIP.)
3. Search. Space between words = AND. Wrap an `"exact phrase"` in quotes.
   Toggle retweets/replies, sort by relevance / date / likes. Each result links
   back to the original tweet.

Your archive is remembered on that device until you hit **reset**.

## Don't want to wait for the archive?

`scrape.js` is a console snippet (same paste-into-console style as
`../removeSpam.js`) that scrapes tweets from a profile or `search?...&f=live`
page as it scrolls, and saves a JSON file you can drop into the app. It's best
effort — X's DOM shifts and rate-limits scrolling. The archive import is the
robust path.

## How it works

- **No dependencies.** ZIP entries are located by parsing the central directory
  and inflated with the browser-native `DecompressionStream('deflate-raw')`. The
  archive is read by *slicing* the file (only the few MB we need — the tweet
  files, not the media), so a large archive is never loaded whole into memory.
  Archives over 4GB (ZIP64) aren't supported — extract `data/tweets.js` and drop
  that instead.
- **Index.** Tweets are tokenized into an in-memory inverted index (token →
  tweet ids) for fast AND queries; phrases fall back to substring matching.
- **Storage.** Normalized tweets live in IndexedDB, so reloads are instant.

Files: `index.html` (UI), `app.js` (DOM + IndexedDB glue), `lib.js` (pure
parse / ZIP / index / search core), `style.css`, `scrape.js` (optional live
scraper).

Tests: `node lib.test.mjs` exercises the parsing, ZIP inflate, indexing, and
search logic (no browser or dependencies needed).
