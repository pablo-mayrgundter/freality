var defaultCellSize = 20;
var white = 'rgb(255,255,255)';
var light = 'rgb(192,192,255)';
var medium = 'rgb(255,192,128)';
var dark = 'rgb(0,64,128)';
var black = 'rgb(0,0,0)';
var game, drawer;

function init() {
  var canvas = getPageElement('gameCanvas');
  var width = canvas.offsetWidth;
  var height = canvas.offsetHeight;
  if (width > height) {
    cellSize = defaultCellSize * (height / width);
  } else {
    cellSize = defaultCellSize * (width / height);
  }
  drawer = new BoardDrawer(canvas, cellSize);
  game = new GOL(drawer.board);
  //var ruleInBoard = new BoardDrawer(get('ruleIn'), cellSize);
  //var ruleOutBoard = new BoardDrawer(get('ruleOut'), cellSize);
};

var running = false;

function animate() {
  if (running) {
    game.runRules();
    drawer.draw();
    drawer.board.next();
  }
};
