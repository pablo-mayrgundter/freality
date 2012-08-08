#import('dart:html');
#import('HexGrid.dart');

/**
 * Wolfram models a fluid as particle interactions on a hexagonal
 * field (p. 378):
 *
 * \ /
 * -x-
 * / \
 *
 * Each cell has incident forces to it at the beginning of each step.
 * The evolution rule specifies how the cell transfers these forces
 * outwards by the end of the step.  Wolfram seems to pick a
 * particular rule which transfers the forces most symmetrically for
 * this configuration, and assumes that the count of incident fields
 * is half, or 3/6.
 *
 * To start simply, I will use a 2-dimensional square grid:
 *
 *  |
 * -x-
 *  |
 *
 * where each node interacts with 4 neighbors.  At a given step, each
 * node has 4 possible states of incoming force per dimension, for a
 * total of 8 incoming states, e.g. for left-right for is either not
 * coming in or coming in from the left only, from the right only, or
 * from both left and right.  Similarly for the up-down dimension.
 * This set of incoming states is encoded as the disjuntion (OR) of
 * those force vectors.  To progress to the next step, absent force is
 * left as absent force, incoming left is propagated to the right, and
 * similarly right to the left, and in the case of both left and right
 * incoming at once, the force is reflected (or doubly passed
 * through).  The full set of states for left-right, before and after,
 * is represented in this table:
 *
 * <pre>Force propagation state transition rules, with encodings for
 * left-right (similarly for up-down):
 *
 *  IN  | OUT | Code
 *  ----------------
 *  -x- | -x- |   00   None
 *  -x< | <x- |   01   Left
 *  >x- | -x> |   10   Right
 *  >x< | <x> |   11   Left OR Right
 * </pre>
 *
 * Solids within the flow are represented with an additional ...
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class Flow {

  String width;
  String num;
  String sleep;
  String hexSize;
  String compareStep;

  int radius, gridScale;
  int space, next;
  var force;
  var frame = null;
  var graphics;
  var palette = null;
  var hexGrid;
  HexGrid grid;

  Flow() {
    width = 80;
    num = 100;
    sleep = 100;
    hexSize = 5;
    compareStep = -1;
    gridScale = 10;
    graphics = query('#canvas').getContext('2d');
    /*
    palette = new Color[64];
    for (int i = 0; i < 64; i++) {
      float val = (float)i / 63.0;
      palette[i] = new Color(val, val, val);
      }*/
    radius = width;
    hexGrid = new HexGrid(radius, radius, gridScale, graphics);
    hexGrid.drawTest();
  }

  void run() {
  }
}

void main() {
  Flow f = new Flow();
  f.run();
}
