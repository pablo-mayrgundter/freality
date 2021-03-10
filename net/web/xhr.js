
export function fetch(url, cb) {
  const req = new XMLHttpRequest();
  req.addEventListener('load', () => {
      cb(req.responseText);
    });
  req.open('GET', url);
  req.send();
}
