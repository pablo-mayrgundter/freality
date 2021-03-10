console.log('Content script');
document.addEventListener('hello', (data) => {
    console.log('Hello listener');
    chrome.runtime.sendMessage('test');
  });
/*
chrome.runtime.sendMessage(
  'aihpepaeafoippecjmjnhbofclplfcmo',
  {word: 'account'},
  foo => {
    console.log('index.js: ', foo);
  });
*/