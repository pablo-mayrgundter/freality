package phys.fluid;

import util.Flags;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/** A grid of vertically-oriented polygons. */
final class HexGridVertical extends HexGrid {

  /**
   * Coordinates for a grid of vertically-oriented polygons.
   *
   *   0 1 2 3 4 5 6 7 8 9 A
   *  0x 1   2     x .   .  
   *  1                     
   *  26     x 3   .       .
   *  3                     
   *  4x 5   4       .   .  
   *  5                     
   *  6.       .   .       .
   *  7                     
   *  8x .   .       .   .
   *
   *  x = origins of neighbor hex
   *  . = vertex of neighbor hex
   *
   *  Thus:
   *      X_STRIDE = 3, Y_STRIDE = 2
   */
  static final int []
    XC = {1,3,4,3,1,0},
    YC = {0,0,2,4,4,2};

  static final int X_STRIDE = 3;
  static final int Y_STRIDE = 2;

  int yOffset;

  HexGridVertical(int cols, int rows, int scale) {
    super(cols, rows, scale, XC, YC, X_STRIDE, Y_STRIDE);
    this.reset();
  }

  @Override
  int getWidth() {
    return xStrideScaled * cols + xStrideScaled / 2;
  }

  @Override
  int getHeight() {
    return yStrideScaled * 2 * rows + 3 * yStrideScaled;
  }

  @Override
  void reset() {
    super.reset();
    yOffset = yStrideScaled;
  }

  @Override
  void next(Color c) {
    super.next(c);
    hex.translate(xStrideScaled, yOffset);
    yOffset *= -1;
  }

  @Override
  void line() {
    hex.translate(-lineWidthPix, yStrideScaled * 2);
    yOffset = Math.abs(yOffset);
    super.line();
  }
}
