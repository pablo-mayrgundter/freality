chrome.runtime.onInstalled.addListener(() => {
    console.log("Wikipedia extension installed.");
  });

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    console.log('onMessage listener: ', request);
    if (request == 'test') {
      let url = 'https://simple.wiktionary.org/wiki/' +
        encodeURIComponent(request);
      fetch(url, {mode: 'cors'}).then(rsp => {
          console.log(rsp);
        });
      return true;  // Will respond asynchronously.
    }
  });
