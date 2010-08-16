var canvasElt;
var canvasWidth, canvasHeight;
var c;
var size = 20, cW, cH;
var rows;
var cols;
var board = new Array();
var white = 'rgb(255,255,255)';
var black = 'rgb(0,0,0)';

function init() {

  canvasElt = document.getElementById('game');
  canvas = canvasElt.getContext('2d');
  cW = canvasElt.width;
  cH = canvasElt.height;
  rows = Math.floor(cH / size);
  cols = Math.floor(cW / size);
  //alert(rows+','+cols);

  for (var y = 0; y < rows; y++) {
    board[y] = new Array();
    for (var x = 0; x < cols; x++) {
      board[y][x] = {'set': 0};
    }
  }

  for (var y = 0; y < rows; y++) {
    for (var x = 0; x < cols; x++) {
      set(x, y, 'color', white);
    }
  }

  addEvent(canvasElt, 'click', clickHandler);
  addEvent(get('button'), 'click', buttonHandler);
};

function clickHandler(evt) {
  var x = evt.x - canvasElt.offsetLeft;
  var y = evt.y - canvasElt.offsetTop;
  var dx = Math.floor(x / cW * cols);
  var dy = Math.floor(y / cH * rows);
  if (isSet(dx, dy, 'color') == black)
    set(dx, dy, 'color', white);
  else
    set(dx, dy, 'color', black);
  draw();
}

function isOn(x, y) {
  return isSet(x, y, 'color') == black;
}
function turnOn(x, y) {
  set(x, y, 'newColor', black);
}
function turnOff(x, y) {
  set(x, y, 'newColor', white);
}

function set(x, y, name, value) {
  if (board[x][y]['newColor'])
    board[x][y]['newColor'] = undefined;
  board[x][y][name] = value;
};

function checkBoard(msg, x, y, name) {
  if (!board)
    alert(msg+' !board');
  else if (!board[x])
    alert(msg+' !board['+x+']');
  else if (!board[x][y])
    alert(msg+' !board['+x+']['+y+']');
  else if (!board[x][y][name])
    alert(msg+' !board['+x+']['+y+']['+name+']');
}

function isSet(x, y, name) {
  //  checkBoard('isSet', x, y, name);
  if (!board[x][y][name])
    return false;
  return board[x][y][name];
};

var running = false;

function buttonHandler() {
  running = !running;
  animate();
}

function animate() {
  runRules();
  draw();
  if (running) {
    setTimeout('animate()', 100);
  }
};

function draw() {
  for (var y = 0; y < rows; y++) {
    for (var x = 0; x < cols; x++) {
      var newColor = isSet(x, y, 'newColor');
      if (newColor)
        set(x, y, 'color', newColor);
      var color = isSet(x, y, 'color');
      canvas.fillStyle = color; 
      canvas.fillRect(size * x, size * y, size, size);
    }
  }
};
