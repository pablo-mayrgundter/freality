var defaultCellSize = 40;
var white = 'rgb(255,255,255)';
var light = 'rgb(192,192,255)';
var medium = 'rgb(255,192,128)';
var dark = 'rgb(0,64,128)';
var black = 'rgb(0,0,0)';
var game, gameBoard;

function init() {
  var gameCanvas = get('game');
  // width > height
  //cellSize = defaultCellSize * (gameCanvas.offsetHeight / gameCanvas.offsetWidth);
  cellSize = defaultCellSize * (gameCanvas.offsetWidth / gameCanvas.offsetHeight);
  gameBoard = new BoardCanvas(gameCanvas, cellSize);
  //var ruleInBoard = new BoardCanvas(get('ruleIn'), cellSize);
  //var ruleOutBoard = new BoardCanvas(get('ruleOut'), cellSize);
  game = new GOL(gameBoard.board);
};

var running = false;

function animate() {
  if (running) {
    game.runRules();
    gameBoard.canvas.draw();
    gameBoard.board.next();
    setTimeout('animate()', 20);
  }
};
