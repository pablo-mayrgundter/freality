#import('dart:html');

#import('/phys/Space.dart');
#import('/phys/Space2D.dart');

#import('/phys/fluid/HexGrid.dart');
#import('/phys/fluid/HexForce.dart');

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
  var frame = null;
  var graphics;
  var palette = null;
  var hexGrid;

  HexGrid grid;
  Space2D space, next;
  Force force;

  Flow() {
    width = 80;
    num = 100;
    sleep = 100;
    hexSize = 5;
    compareStep = -1;
    gridScale = 10;
    graphics = query('#canvas').getContext('2d');
    radius = 10;
    space = new Space2D(radius);
    next = new Space2D(radius);
    force = new HexForce();

    /*
    palette = new Color[64];
    for (int i = 0; i < 64; i++) {
      float val = (float)i / 63.0;
      palette[i] = new Color(val, val, val);
      }*/
    radius = width;
    hexGrid = new HexGrid(radius, radius, gridScale, graphics);
  }

  void run() {
    hexGrid.drawTest();
    while (true) {
      force.apply(space, next);

      Space2D tmp = space;
      space = next;
      next = tmp;
    }
  }
}

void main() {
  Flow f = new Flow();
  f.run();
}
