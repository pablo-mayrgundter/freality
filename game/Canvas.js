function Canvas(elt, board, cellSize) {
  this.canvasElt = elt;
  this.board = board;
  this.cellSize = cellSize;
  this.canvas = this.canvasElt.getContext('2d');
  for (var y = 0; y < board.getHeight(); y++) {
    for (var x = 0; x < board.getWidth(); x++) {
      try {
        this.board.set(x, y, 'age', 0);
        this.drawBorder(x, y, light);
      } catch(e) {
        alert(x+','+y);
        return;
      }
    }
  }
};

Canvas.prototype.draw = function() {
  for (var y = 0; y < this.board.getHeight(); y++) {
    for (var x = 0; x < this.board.getWidth(); x++) {
      var fillColor = white;
      if (this.board.isOn(x, y)) {
        this.board.set(x, y, 'age', 4);
        fillColor = black;
      } else {
        this.board.set(x, y, 'age', this.board.isSet(x, y, 'age') - 1);
      }

      switch(this.board.isSet(x, y, 'age')) {
      case 4: fillColor = black; break;
      case 3: fillColor = dark; break;
      case 2: fillColor = medium; break;
      case 1: fillColor = light;
      }
      this.drawCell(x, y, fillColor);
    }
  }
};

Canvas.prototype.drawCell = function(x, y, color) {
  this.canvas.fillStyle = color;
  this.canvas.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
  this.drawBorder(x, y, light);
};

Canvas.prototype.drawBorder = function(x, y, color) {
  this.canvas.strokeStyle = color;
  this.canvas.strokeRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
};

Canvas.prototype.clickHandler = function() {
  if (window.event.altKey) {
    if (running) {
      running = false;
    } else {
      running = true;
      animate();
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
    me.drawCell(bX, bY, white);
  } else {
    me.board.turnCurOn(bX, bY);
    me.board.turnOn(bX, bY);
    me.drawCell(bX, bY, black);
  }
};
