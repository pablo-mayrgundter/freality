var regionWidth, regionHeight, regionDepth,
  regionXOffset, regionYOffset, regionZOffset,
  xScale, yScale, zScale;
var brushSize = 2;
var maxItr = 10000;
var x = Math.random();
var y = Math.random();
var z = Math.random();
var status;
var points = [];
var ndx = 0;
var zoom = 1;

function ifs() {
  maxItr = 1000;
  xScale = yScale = zScale = 1;
  regionWidth = imageWidth * xScale;
  regionHeight = imageHeight * yScale;
  regionXOffset = imageWidth / 2;
  regionYOffset = imageHeight / 2;
  var foo = Math.PI * 1.0958;
  for(var i = 0; i < maxItr; i++) {
    x += foo * Math.sin(z - x);
    y += foo * Math.cos(y + x);
    z += foo * Math.cos(x - y + z);
    points[i] = [x,y];
  }
  draw();
}

keyLeft = function() {}
keyRight = function () {}
keyUp = function() { erase(); zoom+=0.1; draw(); }
keyDown = function() { erase(); zoom-=0.1; draw(); }

function erase() {
  gfx.fillStyle = 'rgb(0,0,0)';
  gfx.fillRect(0, 0, imageWidth, imageHeight);
}

function draw() {
  gfx.fillStyle = 'rgb(0,0,255)';
  fill();
}

function fill() {
  var xZoom = zoom*xScale;
  var yZoom = zoom*yScale;
  for(var i = 0; i < maxItr; i++) {
    x = points[i][0];
    y = points[i][1];
    var w = x * xZoom + regionXOffset;
    var h = y * yZoom + regionYOffset;
    gfx.fillRect(w, h, brushSize, brushSize);
  }
}
