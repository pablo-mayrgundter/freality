var white = 'rgb(255,255,255)';
var light = 'rgb(192,192,192)';
var medium = 'rgb(128,128,128)';
var dark = 'rgb(64,64,64)';
var black = 'rgb(0,0,0)';

/**
 * The BoardDrawer class draws the logical board to the given HTML
 * canvas.  This class handles user interaction, translating mouse
 * click positions on the canvas to logical click positions on the
 * logical board.  Lastly, the board animation is scheduled when an
 * Option-click is detected.
 *
 * @author Pablo Mayrgundter
 */
class BoardDrawer {

  constructor(board, canvasElt) {
    this.board = board;
    this.canvas = canvasElt.getContext('2d');
    this.canvas.width = canvasElt.offsetWidth;
    this.canvas.height = canvasElt.offsetHeight;
    this.canvas.offsetLeft = canvasElt.offsetLeft;
    this.canvas.offsetTop = canvasElt.offsetTop;
    this.cellSize = this.canvas.width / this.board.width;
    this.animIntervalId = null;

    for (let y = 0; y < this.board.getHeight(); y++) {
      for (let x = 0; x < this.board.getWidth(); x++) {
        try {
          this.board.set(x, y, 'age', 0);
          this.drawBorder(x, y, dark);
        } catch (e) {
          console.log('Error drawing board at coordinate: '+ x +','+ y);
          return;
        }
      }
    }

    this.clickCallback = null;

    canvasElt.onclick = () => {
      this.clickHandler();
    }
  }

  setClickCallback(cb) {
    this.clickCallback = cb;
  }

  draw() {
    for (let y = 0; y < this.board.getHeight(); y++) {
      for (let x = 0; x < this.board.getWidth(); x++) {
        let fillColor = undefined;
        if (this.board.isOn(x, y)) {
          this.board.set(x, y, 'age', 3);
          fillColor = light;
        } else {
          this.board.set(x, y, 'age', Math.max(0, this.board.isSet(x, y, 'age') - 1));
        }

        switch(this.board.isSet(x, y, 'age')) {
        case 3: fillColor = dark; break;
        case 2: fillColor = medium; break;
        case 1: fillColor = light; break;
        case 0: this.board.clear(x, y, 'age');
        }
        if (this.board.isOn(x, y)) {
          fillColor = light;
        }
        if (fillColor) {
          this.drawCell(x, y, fillColor);
        } else {
          this.clearCell(x, y);
        }
      }
    }
  }

  drawCell(x, y, color) {
    this.canvas.fillStyle = color;
    this.canvas.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
    this.drawBorder(x, y, dark);
  }

  clearCell(x, y) {
    this.canvas.clearRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
    this.drawBorder(x, y, dark);
  }

  drawBorder(x, y, color) {
    this.canvas.strokeStyle = color;
    this.canvas.strokeRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
  }

  clickHandler(ev) {
    ev = ev || window.event;
    if (ev.altKey) {
      if (this.animIntervalId) {
        clearInterval(this.animIntervalId);
      } else {
        this.animIntervalId = go();
      }
      return;
    }
    const cX = ev.x - this.canvas.offsetLeft;
    const cY = ev.y - this.canvas.offsetTop;
    const cW = this.canvas.width;
    const cH = this.canvas.height;
    const dX = cX / cW;
    const dY = cY / cH;
    const bW = this.board.getWidth();
    const bH = this.board.getHeight();
    const bX = Math.floor(dX * bW);
    const bY = Math.floor(dY * bH);
    const oldState = this.board.isOn(bX, bY);
    let newState = false;
    if (oldState) {
      this.board.turnCurOff(bX, bY);
      this.board.turnOff(bX, bY);
      this.clearCell(bX, bY);
    } else {
      this.board.turnCurOn(bX, bY);
      this.board.turnOn(bX, bY);
      this.drawCell(bX, bY, light);
      newState = true;
    }
    if (this.clickCallback) {
      this.clickCallback(bX, bY, oldState, newState);
    }
  }
}
