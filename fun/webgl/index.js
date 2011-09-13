// Started with http://learningwebgl.com/blog/?p=28
// and https://developer.mozilla.org/en/WebGL
var gl;
var canvas;
var shader;
var earth, atmos;
var earthTex, atmosTex;

function init(canvasEltId) {
  canvas = $(canvasEltId);

  document.onselectstart = function() {
    return false;
  }; // http://aerotwist.com/lab/ten-things-i-learned/

  gl = initGl(canvas);
  gl.viewportWidth = canvas.width;
  gl.viewportHeight = canvas.height;
  gl.clearColor(0, 0, 0, 1);
  gl.clearDepth(1.0);
  gl.enable(gl.DEPTH_TEST);
  gl.depthFunc(gl.LESS);

  initShaders();
  earthTex = new Texture('earth.jpg');
  earthTex.init();
  atmosTex = new Texture('textures/earth-atmos.jpg');
  atmosTex.init();

  earth = new Sphere(2, earthTex);
  earth.init();
  atmos = new Sphere(2.01, atmosTex);
  atmos.init();

  initKeyHandler();
  initMouseHandler();

  tick();
}

function initGl(canvas) {
  try {
    return canvas.getContext('experimental-webgl');
  } catch (e) {
    alert('Cannot use WebGL :( '+ e);
    return null;
  }
}

var lastTime = 0;

function animate(time) {
  if (lastTime != 0) {
    var elapsed = time - lastTime;
  }
  lastTime = time;
}

var count = 0;
function tick() {
  requestAnimFrame(tick);
  handleKeys();
  var time = new Date().getTime();
  drawScene(time);
  animate(time);
  count++;
}

// Helpers.

function $(id) {
  return document.getElementById(id);
}
