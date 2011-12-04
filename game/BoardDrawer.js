var white = 'rgb(255,255,255)';
var light = 'rgb(192,192,255)';
var medium = 'rgb(255,192,128)';
var dark = 'rgb(0,64,128)';
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
function BoardDrawer(board, canvasElt) {

  this.board = board;
  this.canvas = canvasElt.getContext('2d');
  this.canvas.width = canvasElt.offsetWidth;
  this.canvas.height = canvasElt.offsetHeight;
  this.canvas.offsetLeft = canvasElt.offsetLeft;
  this.canvas.offsetTop = canvasElt.offsetTop;
  this.cellSize = this.canvas.width / this.board.width;
  this.animIntervalId = null;

  for (var y = 0; y < this.board.getHeight(); y++) {
    for (var x = 0; x < this.board.getWidth(); x++) {
      try {
        this.board.set(x, y, 'age', 0);
        this.drawBorder(x, y, dark);
      } catch (e) {
        console.log('Error drawing board at coordinate: '+ x +','+ y);
        return;
      }
    }
  }

  canvasElt.onclick = func(this, this.clickHandler);
}

BoardDrawer.prototype.draw = function() {
  for (var y = 0; y < this.board.getHeight(); y++) {
    for (var x = 0; x < this.board.getWidth(); x++) {
      var fillColor = black;
      if (this.board.isOn(x, y)) {
        this.board.set(x, y, 'age', 4);
        fillColor = white;
      } else {
        this.board.set(x, y, 'age', this.board.isSet(x, y, 'age') - 1);
      }

      switch(this.board.isSet(x, y, 'age')) {
      case 4: fillColor = light; break;
      case 3: fillColor = medium; break;
      case 2: fillColor = dark; break;
      case 1: fillColor = black;
      }
      this.drawCell(x, y, fillColor);
    }
  }
}

BoardDrawer.prototype.drawCell = function(x, y, color) {
  this.canvas.fillStyle = color;
  this.canvas.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
  this.drawBorder(x, y, dark);
}

BoardDrawer.prototype.drawBorder = function(x, y, color) {
  this.canvas.strokeStyle = color;
  this.canvas.strokeRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
}

BoardDrawer.prototype.clickHandler = function(ev) {
  ev = ev || window.event;
  if (ev.altKey) {
    if (this.animIntervalId) {
      clearInterval(this.animIntervalId);
    } else {
      this.animIntervalId = go();
    }
    return;
  }
  var me = this;
  var cX = ev.x - me.canvas.offsetLeft;
  var cY = ev.y - me.canvas.offsetTop;
  var cW = me.canvas.width;
  var cH = me.canvas.height;
  var dX = cX / cW;
  var dY = cY / cH;
  var bW = me.board.getWidth();
  var bH = me.board.getHeight();
  var bX = Math.floor(dX * bW);
  var bY = Math.floor(dY * bH);
  if (me.board.isOn(bX, bY)) {
    me.board.turnCurOff(bX, bY);
    me.board.turnOff(bX, bY);
    me.drawCell(bX, bY, black);
  } else {
    me.board.turnCurOn(bX, bY);
    me.board.turnOn(bX, bY);
    me.drawCell(bX, bY, white);
  }
}
