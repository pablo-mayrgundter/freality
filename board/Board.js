/**
 * The Board class is used to store the logical state of the board.
 * Two arrays are kept, one for the current state of the board, and
 * one as a workspace to setup the new state of the board, e.g. based
 * on the current state.
 *
 * @author Pablo Mayrgundter
 */
function Board(width, height) {
  this.width = width;
  this.height = height;
  this.curCells = new Array();
  this.nxtCells = new Array();

  for (var y = 0; y < height; y++) {
    this.curCells[y] = new Array();
    this.nxtCells[y] = new Array();
    for (var x = 0; x < width; x++) {
      this.curCells[y][x] = {'set': 0};
      this.nxtCells[y][x] = {'set': 0};
    }
  }
};

Board.prototype.getWidth = function() {
  return this.width;
};

Board.prototype.getHeight = function() {
  return this.height;
};

// Logical on/off
Board.prototype.isOn = function(x, y) {
  return this.isSet(x, y, 'on') == 'true';
};

Board.prototype.turnOn = function(x, y) {
  this.set(x, y, 'on', 'true');
};

Board.prototype.turnCurOn = function(x, y) {
  this.setCur(x, y, 'on', 'true');
};

Board.prototype.turnOff = function(x, y) {
  this.set(x, y, 'on', 'false');
};

Board.prototype.turnCurOff = function(x, y) {
  this.setCur(x, y, 'on', 'false');
};

// Inner state methods.
Board.prototype.set = function(x, y, name, value) {
  this.nxtCells[y][x][name] = value;
};

Board.prototype.clear = function(x, y, name) {
  delete this.nxtCells[y][x][name];
};

Board.prototype.setCur = function(x, y, name, value) {
  this.curCells[y][x][name] = value;
};

Board.prototype.isSet = function(x, y, name) {
  //this.checkBoard('isSet', x, y, name);
  if (!this.curCells[y][x][name]) {
    return false;
  }
  return this.curCells[y][x][name];
};

/**
 * Swaps in the next board for the current, and then clears the next
 * board (which was current).
 */
Board.prototype.next = function() {
  // Swap.
  var tmp = this.curCells;
  this.curCells = this.nxtCells;
  this.nxtCells = tmp;
  // Clear next and set to cur.
  for (var y = 0; y < this.height; y++) {
    for (var x = 0; x < this.width; x++) {
      var curObj = this.curCells[y][x];
      var nxtObj = this.nxtCells[y][x] = {}; // Clear.
      for (var prop in curObj) {
        nxtObj[prop] = curObj[prop]; // Set to cur.
      }
    }
  }
};

/*
Board.prototype.checkBoard = function(msg, x, y, name) {
  if (!this.cells)
    alert(msg+' !board');
  else if (!this.cells[y])
    alert(msg+' !cells['+y+']');
  else if (!this.cells[y][x])
    alert(msg+' !cells['+y+']['+x+']');
  else if (!this.cells[y][x][name])
    alert(msg+' !cells['+y+']['+x+']['+name+']');
};
*/
