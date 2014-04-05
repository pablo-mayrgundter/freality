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

  Flow(int this.gridScale) {
    CanvasElement canvas = querySelector('#canvas');
    graphics = canvas.getContext('2d');
    if (gridScale == 1) {
      radius = canvas.width;
    } else {
      radius = (canvas.width / 4 / gridScale).toInt();
    }
    space = new Space2D(radius);
    next = new Space2D(radius);
    force = new HexForce();

    palette = new List<Color>();
    // Only 6 bits can be set in the force field.
    for (int i = 0; i < 7; i++) {
      int val = (255.0 * (i.toDouble() / 6.0)).toInt();
      palette.add(new Color(0, val, val));
    }
    hexGrid = new HexGrid(radius, radius, gridScale, graphics);
  }

  /** Count the number of bits set within the first 6. */
  int popCount(int bits) {
    //if (bits == 0) return 0;
    int popCount = 0;
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
    if (gridScale == 1) {
      ImageData img = hexGrid.getImage();
      var data = img.data;
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          Color pixColor = palette[popCount(space.get([x, y]))];
          int ndx = (y * radius + x) * 4;
          data[ndx] = pixColor.r;
          data[ndx + 1] = pixColor.g;
          data[ndx + 2] = pixColor.b;
          data[ndx + 3] = 255;
        }
      }
      hexGrid.g.putImageData(img, 0, 0);
    } else {
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          hexGrid.next(palette[popCount(space.get([x, y]))]);
        }
        hexGrid.line();
      }
      hexGrid.reset();
    }
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

Flow f;
var animTimer;

void animate(foo) {
  f.run();
}

void runIt() {
  window.console.log('runIt');
  InputElement i = querySelector('#cellSize');
  if (animTimer) {
    animTimer.cancel();
  }
  f = new Flow(int.parse(i.value));
  animTimer = new Timer.periodic(const Duration(milliseconds: 30), animate);
}

void main() {
  ButtonElement b = querySelector('#startButton');
  b.onClick.listen((event) => runIt());
}
