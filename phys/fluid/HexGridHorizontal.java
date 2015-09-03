package phys.fluid;

import util.Flags;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/** A grid of horizontally-oriented polygons. */
final class HexGridHorizontal extends HexGrid {

  /**
   * Coordinates for a grid of horizontally-oriented polygons.
   *
   *   0 1 2 3 4 5 6 7 8
   *  0x   1   x   .   x
   *  16       2       .
   *  2                 
   *  35   x   3       .
   *  4    4       .    
   *  5                 
   *  6x   .       .    
   *  7.       .        
   *
   *  x = origins of neighbor hex
   *  . = vertex of neighbor hex
   *
   *  Thus:
   *      X_STRIDE = 4, Y_STRIDE = 3
   */
  static final int []
    XC = {2,4,4,2,0,0},
    YC = {0,1,3,4,3,1};

  // Reversed from HexGridVertical.
  static final int X_STRIDE = 4;
  static final int Y_STRIDE = 3;
  static final int CELL_WIDTH = 5;
  static final int CELL_HEIGHT = 5;

  static int colsForWidth(int hexWidth, int imgWidth) {
    return imgWidth / (X_STRIDE * hexWidth);
  }

  static int rowsForHeight(int hexHeight, int imgHeight) {
    return imgHeight / (Y_STRIDE * hexHeight);
  }

  int xOffset;

  HexGridHorizontal(int cols, int rows, int scale) {
    super(cols, rows, scale, XC, YC, X_STRIDE, Y_STRIDE);
    this.reset();
  }

  @Override
  int getWidth() {
    return xStrideScaled * cols + xStrideScaled / 2;
  }

  @Override
  int getHeight() {
    return yStrideScaled * rows + 2 * yStrideScaled;
  }

  @Override
  void reset() {
    super.reset();
    xOffset = xStrideScaled / 2;
  }

  @Override
  void next(Color c) {
    super.next(c);
    hex.translate(xStrideScaled, 0);
  }

  @Override
  void line() {
    hex.translate(-lineWidthPix + xOffset, yStrideScaled);
    xOffset *= -1;
    super.line();
  }
}
