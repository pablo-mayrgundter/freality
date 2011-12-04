/**
 * A hardcoded implementation of Conway's Game of Life for testing.
 * Any.js will contain the generic rule system for which GOL is a
 * special case.
 */
function GOL() {
  this.width = 10;
  this.height = 10;
  this.board = null;
}

GOL.prototype.setBoard = function(board) {
  this.board = board;
}

/**
 * From http://en.wikipedia.org/wiki/Conway's_Game_of_Life
 *
 *   A) Any live cell with fewer than two live neighbours dies, as if caused by under-population.
 *   B) Any live cell with two or three live neighbours lives on to the next generation.
 *   C) Any live cell with more than three live neighbours dies, as if by overcrowding.
 *   D) Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
 */
GOL.prototype.runRules = function() {
  var b = this.board;
  for (var y = 1; y < b.getHeight() - 1; y++) {
    for (var x = 1; x < b.getWidth() - 1; x++) {
      var count = 0;
      for (var i = -1; i < 2; i++) {
        for (var j = -1; j < 2; j++) {
          if ((i == 0) && (j == 0))
            continue;
          if (b.isOn(x + j, y + i))
            count++;
        }
      }
      switch (count) {
      case 0: ; break;
      case 1: if (b.isOn(x, y)) { b.turnOff(x, y); }; break; // A
      case 2: if (b.isOn(x, y)) { b.turnOn(x, y); }; break; // B
      case 3: b.turnOn(x, y); break; // B & D
      case 4:;
      case 5:;
      case 6:;
      case 7:;
      case 8: if (b.isOn(x,y)) { b.turnOff(x, y); } ; break; // C
      }
    }
  }
};
