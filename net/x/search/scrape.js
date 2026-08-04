/**
 * Live tweet scraper — an alternative to the full data archive.
 *
 * The archive (Settings -> Your account -> Download an archive of your data) is
 * the complete, reliable source and is what Tweet Archive Search is built for.
 * But if you just want recent tweets without waiting for the archive mail, this
 * scrapes what's on screen as you scroll, the same paste-into-console way as
 * net/x/removeSpam.js.
 *
 * Usage:
 *   1. Open your profile, or a search like
 *        https://x.com/search?q=from%3Apmayrgundter%20climate&f=live
 *   2. Open DevTools console, paste this whole file, press Enter.
 *   3. Run:  scrapeTweets(300)     // auto-scrolls, collects up to ~300 tweets
 *   4. When it finishes it downloads tweets-scraped.json.
 *   5. Drop that JSON onto https://<you>.github.io/net/x/search/
 *
 * Note: X's DOM changes often and rate-limits scrolling; treat this as best
 * effort. The archive import is the robust path.
 */

(() => {
  const byId = new Map();

  function collect() {
    for (const art of document.querySelectorAll('article[data-testid="tweet"]')) {
      const link = art.querySelector('a[href*="/status/"] time')?.closest('a');
      const m = link && link.getAttribute('href').match(/status\/(\d+)/);
      if (!m) continue;
      const id = m[1];
      if (byId.has(id)) continue;

      const textEl = art.querySelector('[data-testid="tweetText"]');
      const timeEl = art.querySelector('time[datetime]');
      const likeEl = art.querySelector('[data-testid="like"], [data-testid="unlike"]');

      byId.set(id, {
        id_str: id,
        full_text: textEl ? textEl.innerText : '',
        created_at: timeEl ? timeEl.getAttribute('datetime') : '',
        favorite_count: likeEl ? parseInt(likeEl.textContent.replace(/[^\d]/g, '') || '0', 10) : 0,
        retweet_count: 0,
      });
    }
  }

  function download() {
    const data = [...byId.values()];
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'tweets-scraped.json';
    a.click();
    console.log(`Saved ${data.length} tweets to tweets-scraped.json`);
    return data;
  }

  // Scroll until we reach `target` tweets or stop finding new ones.
  window.scrapeTweets = function scrapeTweets(target = 300, pause = 1200) {
    let stalls = 0;
    console.log('scrapeTweets: scrolling…');
    const step = () => {
      const before = byId.size;
      collect();
      const after = byId.size;
      console.log(`  collected ${after}`);
      stalls = (after === before) ? stalls + 1 : 0;
      if (after >= target || stalls >= 4) return download();
      window.scrollBy(0, window.innerHeight * 0.9);
      setTimeout(step, pause);
    };
    step();
  };

  // Grab whatever is on screen right now, no scrolling.
  window.scrapeVisible = function scrapeVisible() { collect(); return download(); };

  console.log('Loaded. Run scrapeTweets(300) or scrapeVisible().');
})();
