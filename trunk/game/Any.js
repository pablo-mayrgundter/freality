/**
 * Each cell has implicit and explicit state and rules for modifying
 * both.
 *
 * Implicit state state is analogous to body state, meaning place on
 * the board, proximity to cells with certain other states, etc..
 * This implicit state may be modified by rules but has physical
 * constraints on allowed values and types of transitions.
 *
 * Explicit state is represented as named properties of strings, with
 * no other constraints.
 *
 * Rules specify a substitution system that must only obey the above
 * constraints.
 *
 * --IN PROGRESS--
 */
function Any(board) {
  this.board = board;
}

Any.prototype.runRules = function() {
  var b = this.board;
  for (var y = 1; y < rows - 1; y++) {
    for (var x = 1; x < cols - 1; x++) {
      var rules;
      if (!(rules = b.isSet(x, y, 'rules')))
        return;
      var rules = rules.split(';');
      for (var rule in rules) {
        var subs = rules.split(':');
        var check = subs[0].split(',');
        var apply = subs[1].split(',');
        if (this.allOn(check))
          this.apply(apply);
      }
    }
  }
};

// pseudo below.. need to transform the cell value to a board-modulo position.
Any.prototype.allOn = function(checkCells) {
  for (var cell in checkCells) {
    if (!this.board.isOn(cell))
      return false;
  }
  return true;
};

Any.prototype.apply = function(applyCells) {
  for (var cell in applyCells) {
    if (cell < 0)
      this.board.clearRule(Math.abs(cell));
    else
      this.board.installRule(cell);
  }
};
