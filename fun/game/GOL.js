/**
 * A hardcoded implementation of Conway's Game of Life for testing.
 * Any.js will contain the generic rule system for which GOL is a
 * special case.
 */
function GOL(board) {
  this.board = board;
}

GOL.prototype.runRules = function() {
  var b = this.board;
  for (var y = 1; y < b.getSize() - 1; y++) {
    for (var x = 1; x < b.getSize() - 1; x++) {
      var count = 0;
      for (var i = -1; i < 2; i++)
        for (var j = -1; j < 2; j++)
          if (b.isOn(x + j, y + i)) {
            if (i == 0 && j == 0)
              continue;
            count++;
          }
      switch (count) {
      case 0: if (b.isOn(x,y)) { b.turnOff(x, y) }; break;
      case 1: if (b.isOn(x,y)) { b.turnOff(x, y) }; break;
      case 2: break;
      case 3: if (!b.isOn(x, y)) b.turnOn(x, y); break;
      case 4:;
      case 5: b.turnOff(x, y);
      }
    }
  }
};
