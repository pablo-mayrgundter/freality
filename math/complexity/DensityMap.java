package math.complexity;

import math.util.Linear;

import java.io.Serializable;

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
public class DensityMap implements Serializable {

  final int mWidth, mHeight;
  final int mHalfWidth, mHalfHeight;
  final int [] mDensity;
  int mScale, mMaxDensity;

  public DensityMap(final int width, final int height, final int scale) {
    // width and half width (and height) are used for cartesian
    // orientation.
    mWidth = width;
    mHeight = height;
    mScale = scale;
    mHalfWidth = mWidth / 2;
    mHalfHeight = mHeight / 2;
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
    return mDensity[coordToNdx(x, y)];
  }

  protected final int coordToNdx(double x, double y) {
    x /= mScale;
    y /= mScale;
    x *= (double) mWidth;
    y *= (double) mWidth;
    y *= -1.0;
    final int xc = mHalfWidth + (int) x;
    final int yc = mHalfHeight + (int) y;
    return (int) Linear.squeeze(0, yc * mWidth + xc, mDensity.length - 1);
  }

  public int map(final double x, final double y) {
    final int ndx = coordToNdx(x, y);
    final int val = ++mDensity[ndx];
    if (val > mMaxDensity) {
      mMaxDensity = val;
    }
    return ndx;
  }

  static final long serialVersionUID = 2486513494901805181L;
}
