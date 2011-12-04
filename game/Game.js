var game = null;

function Game(rules, canvas) {
  this.rules = rules;
  this.board = new Board(rules.width, rules.height);
  this.rules.setBoard(this.board);
  this.drawer = new BoardDrawer(this.board, canvas);
  game = this;
}

function go() {
  setInterval('game.animate()', 20);
}

Game.prototype.animate = function() {
  this.rules.runRules();
  this.drawer.draw();
  this.drawer.board.next();
}
