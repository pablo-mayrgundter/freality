const AND = '&';
const ASSIGN = '=';

/**
 * @param hostObj Optional object to use to store params.  If provided
 * it will also be returned.  Otherwise a new object will be returned.
 * @return Object representation of url-style parameters that are
 * present in location.hash. */
export function getHashParams(hostObj) {
  const hash = location.hash.substr(1);
  if (!hash) {
    return null;
  }
  return hash.split(AND).reduce((params, item) => {
      const parts = item.split(ASSIGN);
      params[parts[0]] = parts[1];
      return params;
    }, hostObj || {});
}

/** Sets location.hash to the given params encoded as a url-parameter
 * style string. */
export function setHashParams(params) {
  const arr = [];
  for (let i in params) {
    arr.push(i + ASSIGN + params[i]);
  }
  location.hash = arr.join(AND);
}

