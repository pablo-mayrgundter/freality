class MusicBox {
  constructor(width, height, canvasId) {
    this.width = width;
    this.height = height;
    this.step = 0;
    this.board = new Board(width, height);
    this.drawer = new BoardDrawer(this.board, document.getElementById(canvasId));
    this.drawer.setClickCallback(this.clickCallback);
    this.isOn = false;
  }

  clickCallback(x, y, wasOn, isOn) {
    console.log(`${x}, ${y}, ${wasOn}, ${isOn}`);
  }

  animate() {
    if (this.step >= this.width * this.height) {
      this.step = 0;
    }
    const curX = this.step % this.width;
    const curY = Math.floor(this.step / this.width);
    this.isOn = this.board.isOn(curX, curY);
    if (this.isOn) {
      pulse();
    }
    this.board.turnOn(curX, curY);
    this.drawer.draw();
    this.board.next();
    if (!this.isOn) {
      this.board.turnOff(curX, curY);
    }
    this.step++;
  }
}
