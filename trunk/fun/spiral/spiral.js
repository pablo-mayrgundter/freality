var imageWidth, imageHeight, imageDepth;
// Set these in delegates.
var regionWidth, regionHeight, regionDepth,
  regionXOffset, regionYOffset, regionZOffset,
  xScale, yScale, zScale;
var brushSize = 2;
var gfx;
var x = Math.random();
var y = Math.random();
var z = Math.random();
var status;
var r = g = b =255;
var maxItr = 10000;
var points = [];
var ndx = 0;

function init() {
  var canvas = document.getElementById('canvas');
  status = document.getElementById('status');
  imageWidth = canvas.clientWidth;
  imageHeight = canvas.clientHeight;
  gfx = canvas.getContext('2d');
  gfx.fillStyle = 'black';
  gfx.fillRect(0, 0, imageWidth, imageHeight);
  gfx.fillStyle = 'rgb(0,0,255)';
  canvas.onclick = function(e){
  };
  // Bind to document key handlers.
  document.onkeydown = function(e){
    handleKeyEvent(e, true);
  };
  document.onkeyup = function(e){
    handleKeyEvent(e, false);
  };
  count = 0;
  //mandelbrot();
  ifs();
}

function handleKeyEvent(e) {
  switch (e.keyCode) {
  case 37: keyLeft();break;
  case 38: keyUp();break;
  case 39: keyRight();break;
  case 40: keyDown();break;
  }
}
