/**
 * A hardcoded implementation of Conway's Game of Life for testing.
 * Any.js (will) contains the generic rule system for which GOL is a
 * special case.
 */
function runRules() {
  for (var y = 1; y < rows - 1; y++) {
    for (var x = 1; x < cols - 1; x++) {
      var count = 0;
      for (var i = -1; i < 2; i++)
        for (var j = -1; j < 2; j++)
          if (isOn(x + j, y + i)) {
            if (i == 0 && j == 0)
              continue;
            count++;
          }
      switch (count) {
      case 0: ;
      case 1: turnOff(x, y); break;
      case 2: break;
      case 3: if (!isOn(x, y)) turnOn(x, y); break;
      case 4:;
      case 5: turnOff(x, y);
      }
    }
  }
};
