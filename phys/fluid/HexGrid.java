package phys.fluid;

import util.Flags;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * Hexagonal grid and image.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
final class HexGrid {

  static final boolean DRAW_BORDER = new Flags(HexGrid.class).get("drawBorder", false);

  /**
   * Coordinates of a polygon.
   */
  int []
    xc = {1,3,4,3,1,0},
    yc = {0,0,2,4,4,2};

  BufferedImage img;
  Graphics g;
  int cols, rows, scale, count, xStride, yStride, yOffset;
  Polygon hex;

  HexGrid(int columns, int rows, int scale) {
    this.cols = columns;
    this.rows = rows;
    this.scale = scale;
    for (int i = 0; i < 6; i++) {
      xc[i] = xc[i] * scale;
      yc[i] = yc[i] * scale;
    }
    hex = new Polygon(xc, yc, 6);
    xStride = 3 * scale;
    yStride = 2 * scale;
    yOffset = yStride;
    img = new BufferedImage(xStride * cols + xStride / 2,
                            2 * yStride * rows + yStride,
                            BufferedImage.TYPE_INT_RGB);
    g = img.getGraphics();
    count = 0;
  }

  void drawTest() {
    for (int i = 0, I = cols; i < I; i++) {
      for (int j = 0, J = rows; j < J; j++) {
        next(new Color(0,
                       0.5f + (0.5f * i / (float)I),
                       0.5f + (0.5f * j / (float)J)));
      }
      line();
    }
  }

  void reset() {
    hex = new Polygon(xc, yc, 6);
    yOffset = yStride;
    count = 0;
  }

  void next(Color c) {
    g.setColor(c);
    g.fillPolygon(hex);
    if (DRAW_BORDER) {
      g.setColor(Color.BLUE);
      g.drawPolygon(hex);
    }
    hex.translate(xStride, yOffset);
    yOffset *= -1;
    count++;
  }

  void line() {
    // if yOffset is now negative, it was positive for last
    // translate, so only add half a yStride.  Otherwise, it was
    // negative for last translate, so add 1.5 strides.
    hex.translate(-1 * (count * xStride),
                  yOffset < 0 ? yStride : 2 * yStride);
    yOffset = Math.abs(yOffset);
    count = 0;
  }
}
