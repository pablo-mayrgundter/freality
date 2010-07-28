var canvasElt;
var canvasWidth, canvasHeight;
var c;
var size = 20;
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

  for (var r = 0; r < rows; r++) {
    board[r] = new Array();
    for (var c = 0; c < cols; c++) {
      board[r][c] = {'set': 0};
    }
  }

  for (var r = 0; r < rows; r++) {
    for (var c = 0; c < cols; c++) {
      set(r, c, 'color', white);
    }
  }

  addEvent(canvasElt, 'click', clickHandler);
  addEvent(get('button'), 'click', stepHandler);
  inited = 1;
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

function isOn(r, c) {
  return isSet(r, c, 'color') == black;
}
function turnOn(r, c) {
  set(r, c, 'newColor', black);
}
function turnOff(r, c) {
  set(r, c, 'newColor', white);
}

var inited = 0;
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

function stepHandler(evt) {
  for (var r = 1; r < rows - 1; r++) {
    for (var c = 1; c < cols - 1; c++) {
      var count = 0;
      for (var i = -1; i < 2; i++)
        for (var j = -1; j < 2; j++)
          if (isOn(r + i, c + j)) {
            if (i == 0 && j == 0)
              continue;
            count++;
          }
      switch (count) {
      case 0: ;
      case 1: turnOff(r, c); break;
      case 2: break;
      case 3: if (!isOn(r, c)) turnOn(r, c); break;
      case 4:;
      case 5: turnOff(r, c);
      }
    }
  }
  draw();
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

function onclick() {
  var event = window.event;
  set(event.x, event.y, 'color', 'rgb(0,0,0)');
  draw();
};
