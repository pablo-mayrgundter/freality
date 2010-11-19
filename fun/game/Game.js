var canvas, game, board;
var white = 'rgb(255,255,255)';
var light = 'rgb(192,192,255)';
var medium = 'rgb(255,192,128)';
var dark = 'rgb(0,64,128)';
var black = 'rgb(0,0,0)';
var cellSize = 20, inset = 0;

function init() {
  var canvasElt = document.getElementById('game');
  //canvasElt.style.width = (window.innerWidth - inset) +'px';
  //canvasElt.style.height = (window.innerHeight - inset) +'px';
  canvasElt.style.width = '1280px';
  canvasElt.style.height = '800px'
  var width = canvasElt.clientWidth / cellSize;
  var height = canvasElt.clientHeight / cellSize;
  board = new Board(width, height);
  canvas = new Canvas(canvasElt, board, cellSize);
  game = new GOL(board);
  addEvent(canvasElt, 'click', func(canvas, canvas.clickHandler));
  board.turnOn(3, 1);
  board.turnOn(3, 2);
  board.turnOn(3, 3);
  board.turnOn(2, 3);
  board.turnOn(1, 2);
  board.next();
  canvas.draw();
};

var running = false;

function animate() {
  if (running) {
    game.runRules();
    canvas.draw();
    board.next();
    setTimeout('animate()', 20);
  }
};
