class BoardDemo {
  constructor(width, height, canvasId) {
    this.board = new Board(width, height);
    this.drawer = new BoardDrawer(this.board, document.getElementById(canvasId));
  }
  run() {
    this.board.set(0, 0);
    this.drawer.draw();
  }
}
