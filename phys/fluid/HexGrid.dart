#library('phys/fluid/HexGrid');
//#import('../../util/Flags.dart');

/**
 * Hexagonal grid and image.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class HexGrid {

  final bool DRAW_BORDER = false;
  final double TAU = Math.PI * 2;

  /**
   * Coordinates of a polygon.
   */
  /*
  xc = new List<int>();
  yc = new List<int>();
  xc[0] = {1,3,4,3,1,0},
  yc = {0,0,2,4,4,2};
  */

  var g;
  int cols, rows, scale, count, xStride, yStride, yOffset;
  //Polygon hex;
  int x, y;
  int radius;

  HexGrid(int columns, int rows, int scale, var graphics) {
    this.cols = columns;
    this.rows = rows;
    this.scale = scale;
    this.g = graphics;
    /*
    for (int i = 0; i < 6; i++) {
      xc[i] = xc[i] * scale;
      yc[i] = yc[i] * scale;
    }
    */
    x = 0;
    y = 0;
    // hex = new Polygon(xc, yc, 6);
    xStride = 3 * scale;
    yStride = 2 * scale;
    yOffset = yStride;
    count = 0;
    radius = scale;
  }

  void drawTest() {
    var color = 'black';
    for (int i = 0, I = cols; i < I; i++) {
      for (int j = 0, J = rows; j < J; j++) {
        //next(new Color(0,
        // 0.5f + (0.5f * i / (float)I),
        // 0.5f + (0.5f * j / (float)J)));
        next(color);
      }
      line();
    }
  }

  void reset() {
    //hex = new Polygon(xc, yc, 6);
    x = 0;
    y = 0;
    yOffset = yStride;
    count = 0;
  }

  void next(String color) {
    g.beginPath();
    g.fillStyle = color;
    //g.fillPolygon(hex);
    g.arc(x, y, radius, 0, TAU, false);
    if (DRAW_BORDER) {
      g.fillStyle = 'blue';
      //g.drawPolygon(hex);
      var radius = 3;
      g.arc(x, y, radius, 0, TAU, false);
    }
    g.fill();
    g.closePath();
    g.stroke();
    x += xStride;
    y += yOffset;
    yOffset *= -1;
    count++;
  }

  void line() {
    // if yOffset is now negative, it was positive for last
    // translate, so only add half a yStride.  Otherwise, it was
    // negative for last translate, so add 1.5 strides.
    x += -1 * (count * xStride);
    y += yOffset < 0 ? yStride : 2 * yStride;
    yOffset = yOffset < 0 ? -yOffset : yOffset;
    count = 0;
  }
}
