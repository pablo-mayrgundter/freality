const AND = '&';
const ASSIGN = '=';

function getHashParams() {
  const hash = location.hash.substr(1);
  if (!hash) {
    return null;
  }
  return hash.split(AND).reduce((params, item) => {
      const parts = item.split(ASSIGN);
      params[parts[0]] = parts[1];
      return params;
    }, {});
}

function setHashParams(params) {
  const arr = [];
  for (let i in params) {
    arr.push(i + ASSIGN + params[i]);
  }
  location.hash = arr.join(AND);
}
