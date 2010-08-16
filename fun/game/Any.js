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
function Rules(args) {
  var rules = args.split(';');
  var uVal = parts[0];
  var rVal = parts[1];
  var dVal = parts[2];
  var lVal = parts[3];
  var ruleCurVal = parts[4];
  var ruleNxtVal = parts[5];
  for (var rule in rules) {
    if (dir == 'up') {
      var cur = get(x, y, 'val');
      if (cur == ruleCurVal) {
        set(x, y, 'val', ruleNxtVal);
      }
    }
  }
}

function runRules() {
  for (var y = 1; y < rows - 1; y++) {
    for (var x = 1; x < cols - 1; x++) {
      var rules = new Rules(get(x, y, 'rules'));
      rules.apply();
    }
  }
};
