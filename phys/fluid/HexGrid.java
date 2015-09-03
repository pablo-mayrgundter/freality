package phys.fluid;

import util.Flags;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * The HexGrid class provides a grid of hexagons that can be drawn
 * line-by-line, varying the color of each cell, for use in
 * visualizing the dynamics of hexagonal cellular automata.
 *
 * @see HexGridHorizontal
 * @see HexGridVertical
 */
abstract class HexGrid {

  static final boolean DRAW_BORDER = new Flags(HexGrid.class).get("drawBorder", true);

  BufferedImage img;
  Graphics g;
  int cols, rows, scale, count, xStrideScaled, yStrideScaled;
  int lineWidthPix;
  Polygon hex;
  final int []
    xc = new int[6],
    yc = new int[6];

  HexGrid(int cols, int rows, int scale,
          int [] XC, int [] YC,
          int xStride, int yStride) {
    System.out.printf("cols: %d, rows: %d, scale: %d\n", cols, rows, scale);
    this.cols = cols;
    this.rows = rows;
    this.scale = scale;
    for (int i = 0; i < 6; i++) {
      xc[i] = XC[i] * scale;
      yc[i] = YC[i] * scale;
    }
    hex = new Polygon(xc, yc, 6);
    xStrideScaled = xStride * scale;
    yStrideScaled = yStride * scale;
    lineWidthPix = cols * xStrideScaled;
    System.out.printf("xStrideScaled: %d, yStrideScaled: %d\n",
                      xStrideScaled, yStrideScaled);
    img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    g = img.getGraphics();
    count = 0;
  }

  abstract int getWidth();
  abstract int getHeight();

  void reset() {
    hex = new Polygon(xc, yc, 6);
    count = 0;
  }

  void next(Color c) {
    g.setColor(c);
    g.fillPolygon(hex);
    if (DRAW_BORDER) {
      g.setColor(Color.BLACK);
      g.drawPolygon(hex);
    }
    count++;
  }

  void line() {
    count = 0;
  }

  private void drawTest() {
    for (int i = 0, I = cols; i < I; i++) {
      for (int j = 0, J = rows; j < J; j++) {
        next(new Color(1f,
                       0.5f + (0.5f * i / (float)I),
                       0.5f + (0.5f * j / (float)J)));
      }
      line();
    }
  }
}
