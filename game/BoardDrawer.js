/**
 * The BoardDrawer class draws the logical board to the given HTML
 * canvas.  This class handles user interaction, translating mouse
 * click positions on the canvas to logical click positions on the
 * logical board.  Lastly, the board animation is scheduled when an
 * Option-click is detected.
 *
 * @author Pablo Mayrgundter
 */
function BoardDrawer(canvasElt, cellSize) {

  this.canvasElt = canvasElt;
  this.width = canvasElt.offsetWidth / cellSize;
  this.height = canvasElt.offsetHeight / cellSize;
  this.board = new Board(this.width, this.height);
  this.cellSize = cellSize;
  this.canvas = this.canvasElt.getContext('2d');
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

  addEvent(this.canvasElt, 'click', func(this, this.clickHandler));

};

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
};

BoardDrawer.prototype.drawCell = function(x, y, color) {
  this.canvas.fillStyle = color;
  this.canvas.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
  this.drawBorder(x, y, dark);
};

BoardDrawer.prototype.drawBorder = function(x, y, color) {
  this.canvas.strokeStyle = color;
  this.canvas.strokeRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
};

BoardDrawer.prototype.clickHandler = function() {
  if (window.event.altKey) {
    if (running) {
      running = false;
      clearInterval(this.animIntervalId);
    } else {
      running = true;
      this.animIntervalId = setInterval('animate()', 20);
    }
    return;
  }
  var me = this;
  var evt = window.event;
  var cX = evt.x - me.canvasElt.offsetLeft;
  var cY = evt.y - me.canvasElt.offsetTop;
  var cW = me.canvasElt.offsetWidth;
  var cH = me.canvasElt.offsetHeight;
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
};
