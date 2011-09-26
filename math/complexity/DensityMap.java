package math.complexity;

import math.util.Linear;

/**
 * The DensityMap class is a two dimensional density histogram
 * oriented on a cartesian plane.  Coordinate scalars are divided by
 * the integer "scale" system property and squeezed between positive
 * and negative scale.  Coordinates may also be offset by the integer
 * properties "xoff" and "yoff".  A maximum density for the entire map
 * is kept for retrieval after population.
 *
 * @author Pablo Mayrgundter
 */
class DensityMap {

  static final int SCALE = Integer.parseInt(System.getProperty("scale", "100"));
  static final int XOFF = Integer.parseInt(System.getProperty("xoff", "0"));
  static final int YOFF = Integer.parseInt(System.getProperty("yoff", "0"));

  final int mWidth, mHeight;
  final float mHalfWidth, mHalfHeight;
  final int [] mDensity;
  int mMaxDensity;

  DensityMap(final int width, final int height) {
    // width and half width (and height) are used for cartesian
    // orientation.
    mWidth = width;
    mHeight = height;
    mHalfWidth = mWidth / 2f;
    mHalfHeight = mHeight / 2f;
    mDensity = new int[mWidth * mHeight];
  }

  public void clear() {
    java.util.Arrays.fill(mDensity, 0);
    mMaxDensity = 0;
  }

  public int getMaxDensity() {
    return mMaxDensity;
  }

  public int getDensity(final double x, final double y) {
    return coordToNdx(x, y);
  }

  protected final int coordToNdx(final double x, final double y) {
    final int xc =
      Linear.squeezeToInt(0,
                          mHalfWidth + Linear.squeeze(-SCALE, x / SCALE, SCALE) * mWidth + XOFF,
                          mWidth - 1);
    final int yc =
      Linear.squeezeToInt(0,
                          mHalfHeight - Linear.squeeze(-SCALE, y / SCALE, SCALE) * mHeight + YOFF,
                          mHeight - 1);
    return yc * mWidth + xc;
  }

  public int map(final double x, final double y) {
    final int ndx = coordToNdx(x, y);
    mDensity[ndx]++;
    if (mDensity[ndx] > mMaxDensity) {
      mMaxDensity = mDensity[ndx];
    }
    return ndx;
  }
}
