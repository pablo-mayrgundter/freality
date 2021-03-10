/**
 * Initialize the sigma graph library, construct a graph with numNodes
 * and perform the first frame rendering.
 */
function init() {
  /*
  let event = document.createEvent('Event');
  event.initEvent('hello');
  document.dispatchEvent(event);
  */
  /*
  chrome.runtime.sendMessage('aihpepaeafoippecjmjnhbofclplfcmo', {foo: 'bar'}, null, () => {
      console.log('main page cb');
    });
  */
      let url = 'http://simple.wiktionary.org/wiki/' +
        encodeURIComponent('hello');
      fetch(url, {mode: 'cors'}).then(rsp => {
          console.log(rsp);
        });
  if (true) return;
  const s = new sigma('canvas');
  let lastNode;
  for (let i = 0; i < 10; i++) {
    s.graph.addNode({
        id: i,
        label: `${i}`,
        x: Math.random()*10,
        y: Math.random()*10,
        size: 3});
    if (typeof lastNode == 'number') {
      s.graph.addEdge({id: i, source: i, target: lastNode});
    }
    lastNode = i;
  }
  s.refresh();
}
