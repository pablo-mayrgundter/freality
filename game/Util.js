// Generic dom utils.
function getPageElement(id) {
  return document.getElementById(id);
}

function func(obj, handler, args) {
  args = args || [];
  return function() { handler.apply(obj, args); };
}

function d(msg) {
  console.log(msg);
}
