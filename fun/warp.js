var s;
var id, last;
var x, y, z;
var warpFactor;
var numNodes = 300;
var colorMult = 256;

function init() {
  s = sigma.init(document.getElementById('body'));
  id = 0;
  last = null;
  x = y = z = 0;
  warpFactor = Math.random() * 0.01;

  for (var i = 0; i < numNodes; i++) {
    s.addNode(i, {id:i,x:0,y:0,size:3});
    // Node and an edge to last node.
    if (last) {
      s.addEdge(i, last, i, {size:3});
    }
    last = i;
  }

  id = 0;
  for (var i = 0; i < 5; i++) {
    attractor();
  }  
  s.iterNodes(function(n){
      warp(n);
    });

  s.draw();
}

// The shape.
function attractor() {
  x = Math.sin(y + z) * Math.sin(y + warpFactor);
  y = Math.cos(x - z) * Math.cos(x - warpFactor);
  z = id + warpFactor;
  id++;
}

function warp(n) {
  attractor();

  // Move the node to its new position
  n.x = x;
  n.y = y;
  // Color mapping.
  var r = Math.abs(n.x) * colorMult,
    g = Math.abs(n.y) * colorMult,
    b = Math.abs(n.x + n.y / 2.0) * colorMult;
  n.color = sigma.tools.rgbToHex(r, g, b);
}

var interval;
function anim() {
  if (interval) {
    clearInterval(interval);
    interval = null;
  } else {
    interval = setInterval('frame()', 30);
  }
}

function frame() {
  id = 0;
  warpFactor += 0.01;
  for (var i = 0; i < 10; i++) {
    attractor();
  }  
  s.iterNodes(warp);
  s.draw(2, 2, 2, 2);
}
