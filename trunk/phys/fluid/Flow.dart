part of phys;

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

  int radius, gridScale;
  var frame = null;
  var graphics;
  List<Color> palette = null;
  var hexGrid;

  HexGrid grid;
  Space2D space, next;
  Force force;

  Flow() {
    gridScale = 5;
    CanvasElement canvas = querySelector('#canvas');
    graphics = canvas.getContext('2d');
    radius = (canvas.width / 4 / gridScale).toInt();
    space = new Space2D(radius);
    next = new Space2D(radius);
    force = new HexForce();

    palette = new List<Color>();
    // Only 6 bits can be set in the force field.
    for (int i = 0; i <= 6; i++) {
      int val = (256.0 * (i.toDouble() / 7.0)).toInt();
      palette.add(new Color(val, 0, val));
    }
    hexGrid = new HexGrid(radius, radius, gridScale, graphics);
  }

  int popCount(int bits) {
    int popCount = 0;
    // Only go up to 6 for this force field.
    for (int i = 0; i < 6; i++) {
      popCount += bits & 1;
      bits >>= 1;
    }
    return popCount;
  }

  void run() {
    InputElement e = querySelector('#wallLeftR');
    double wallLeftR = double.parse(e.value);
    int wallLeft = (radius.toDouble() * wallLeftR).toInt();
    int wallLo = (radius.toDouble() * 0.25).toInt();
    int wallHi = (radius.toDouble() * 0.75).toInt();
    int forceRight =
      HexForce.R |
      HexForce.UR |
      HexForce.DR,
      forceWall =
      HexForce.L |
      HexForce.UL |
      HexForce.DL;
    // draw.
    for (int y = 0; y < radius; y++) {
      for (int x = 0; x < radius; x++) {
        hexGrid.next(palette[popCount(space.get([x, y]))]);
      }
      hexGrid.line();
    }
    hexGrid.reset();
    force.apply(space, next);
    for (int y = 0; y < radius; y++) {
      // Continuous left-to-right feed from the left side.
      next.set(forceRight, [0, y]);
    }
    for (int y = 0; y < radius; y++) {
      // Continuous left-to-right feed from the left side.
      next.set(forceRight, [0, y]);
      // wall should bounce back.
      if (y >= wallLo && y <= wallHi) {
        next.set(forceWall, [wallLeft, y]);
      }
    }
    Space2D tmp = space;
    space = next;
    next = tmp;
  }
}

Flow f = new Flow();

void runIt(foo) {
  f.run();
}

void main() {
  new Timer.periodic(const Duration(milliseconds: 100), runIt);
}