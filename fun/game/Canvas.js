var white = 'rgb(255,255,255)';
var light = 'rgb(255,192,192)';
var medium = 'rgb(192,192,64)';
var dark = 'rgb(64,64,192)';
var black = 'rgb(0,0,255)';
var green = 'rgb(0,255,0)';

function Canvas(board) {
  this.board = board;
  this.canvasElt = document.getElementById('game');
  this.canvas = this.canvasElt.getContext('2d');
  this.cW = this.canvasElt.width;
  this.cH = this.canvasElt.height;
  this.rows = Math.floor(this.cH / this.size);
  this.cols = Math.floor(this.cW / this.size);
  this.size = board.getSize();
  for (var y = 0; y < this.size; y++) {
    for (var x = 0; x < this.size; x++) {
      this.board.set(x, y, 'age', 0);
    }
  }
};

Canvas.prototype.draw = function(expire) {
  for (var y = 0; y < this.size; y++) {
    for (var x = 0; x < this.size; x++) {
      var fillColor = white, age;
      if (this.board.isOn(x, y)) {
        fillColor = black;
        this.board.set(x, y, 'age', 4);
      } else if (expire) {
        var age = this.board.isSet(x, y, 'age');
        this.board.set(x, y, 'age', --age)
        switch(age) {
        case 3: fillColor = dark; break;
        case 2: fillColor = medium; break;
        case 1: fillColor = light; break;
        default:
          fillColor = white;
          this.board.set(x, y, 'age', 0);
        }
      }
      this.canvas.fillStyle = fillColor;
      this.canvas.fillRect(this.size * x, this.size * y, this.size, this.size);
    }
  }
};

Canvas.prototype.clickHandler = function() {
  var evt = window.event;
  var x = evt.x - this.canvasElt.offsetLeft;
  var y = evt.y - this.canvasElt.offsetTop;
  var dx = Math.floor(x / this.cW * this.size);
  var dy = Math.floor(y / this.cH * this.size);
  if (this.board.isOn(dx, dy))
    this.board.turnOff(dx, dy);
  else
    this.board.turnOn(dx, dy);
  this.board.next();
  this.draw(false);
};
