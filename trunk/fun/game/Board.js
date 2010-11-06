function Board(size) {
  this.size = size;
  this.curCells = new Array();
  this.nxtCells = new Array();

  for (var y = 0; y < size; y++) {
    this.curCells[y] = new Array();
    this.nxtCells[y] = new Array();
    for (var x = 0; x < size; x++) {
      this.curCells[y][x] = {'set': 0};
      this.nxtCells[y][x] = {'set': 0};
    }
  }
};

Board.prototype.getSize = function() {
  return this.size;
};

// Logical on/off
Board.prototype.isOn = function(x, y) {
  return this.isSet(x, y, 'on') == 'true';
};

Board.prototype.turnOn = function(x, y) {
  this.set(x, y, 'on', 'true');
};

Board.prototype.turnOff = function(x, y) {
  this.set(x, y, 'on', 'false');
};

// Inner state methods.
Board.prototype.set = function(x, y, name, value) {
  this.nxtCells[x][y][name] = value;
};

Board.prototype.isSet = function(x, y, name) {
  //this.checkBoard('isSet', x, y, name);
  if (!this.curCells[x][y][name])
    return false;
  return this.curCells[x][y][name];
};

Board.prototype.next = function() {
  // Swap.
  var tmp = this.curCells;
  this.curCells = this.nxtCells;
  this.nxtCells = tmp;
  // Clear next and set to cur.
  for (var y = 0; y < this.size; y++) {
    for (var x = 0; x < this.size; x++) {
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
  else if (!this.cells[x])
    alert(msg+' !cells['+x+']');
  else if (!this.cells[x][y])
    alert(msg+' !cells['+x+']['+y+']');
  else if (!this.cells[x][y][name])
    alert(msg+' !cells['+x+']['+y+']['+name+']');
};
*/