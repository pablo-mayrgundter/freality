var canvas, game;

function init() {
  var boardSize = 25;
  var board = new Board(boardSize);
  canvas = new Canvas(board);
  game = new GOL(board);
  addEvent(canvas.canvasElt, 'click', func(canvas, canvas.clickHandler));
  addEvent(get('button'), 'click', buttonHandler);
};

var running = false;

function buttonHandler() {
  running = !running;
  animate();
};

function animate() {
  game.runRules();
  canvas.draw();
  if (running) {
    setTimeout('animate()', 100);
  }
};
