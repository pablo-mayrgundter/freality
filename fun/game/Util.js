// Generic dom utils.
function get(id) {
  return document.getElementById(id);
}

function func(obj, handler, args) {
  if (!args)
    args = [];
  return function() { handler.apply(obj, args); };
}

function addEvent( obj, type, fn ) {
  if ( obj.attachEvent ) {
    obj['e'+type+fn] = fn;
    obj[type+fn] = function(){obj['e'+type+fn]( window.event );}
    obj.attachEvent( 'on'+type, obj[type+fn] );
  } else {
    obj.addEventListener( type, fn, false );
  }
}

var debugElt;
function d(msg) {
  if (null == debugElt) {
    debugElt = get('debug');
    if (null == debugElt)
      return;
  }
  debugElt.innerHTML += msg + '<br/>';
}
