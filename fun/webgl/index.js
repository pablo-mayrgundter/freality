// Started with http://learningwebgl.com/blog/?p=28
// and https://developer.mozilla.org/en/WebGL
var gl;
var canvas;
var shader;

function init() {
  canvas = $('canvas');
  var ex;
  try {
    gl = canvas.getContext('experimental-webgl');
  } catch (e) {
    ex = e;
  }
  if (!gl) {
    alert('Cannot use WebGL :( '+ e);
    return;
  }
  gl.viewportWidth = canvas.width;
  gl.viewportHeight = canvas.height;

  gl.clearColor(0, 0, 0, 1);
  gl.clearDepth(1.0);
  gl.enable(gl.DEPTH_TEST);
  gl.depthFunc(gl.LEQUAL);

  initShaders();
  initShape();
  initTexture();
  initKeyHandler();
  initMouseHandler();

  tick();
}

var lastTime = 0;
var rotTri = 0;
var rotSqu = 0;

function animate(time) {
  if (lastTime != 0) {
    var elapsed = time - lastTime;
    rotTri += (90 * elapsed) / 1000.0;
    rotSqu += (75 * elapsed) / 1000.0;
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
