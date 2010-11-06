var canvas, game, board;

function init() {
  var boardSize = 25;
  board = new Board(boardSize);
  canvas = new Canvas(board);
  game = new GOL(board);
  addEvent(canvas.canvasElt, 'click', func(canvas, canvas.clickHandler));
  addEvent(get('button'), 'click', buttonHandler);
};

var running = false;

function buttonHandler(evt) {
  if (evt) {
    if (running)
      evt.target.innerHTML = 'start';
    else
      evt.target.innerHTML = 'stop';
  }
  running = !running;
  animate();
};

function animate() {
  game.runRules();
  canvas.draw(true);
  board.next();
  if (running) {
    setTimeout('animate()', 500);
  }
};
