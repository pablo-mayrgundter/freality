package math.complexity;

import math.util.Linear;

class DensityMap {

  static final int SCALE = Integer.parseInt(System.getProperty("scale", "100"));
  static final int XOFF = Integer.parseInt(System.getProperty("xoff", "0"));
  static final int YOFF = Integer.parseInt(System.getProperty("yoff", "0"));

  final int mWidth, mHeight;
  final float mHalfWidth, mHalfHeight;
  final int [] mDensity;
  int mMaxDensity;

  DensityMap(final int width, final int height) {
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

  int map(final double x, final double y) {
    final int xc = Linear.squeezeToInt(0, mHalfWidth + Linear.squeeze(-SCALE, x / SCALE, SCALE) * mWidth + XOFF, mWidth - 1);
    final int yc = Linear.squeezeToInt(0, mHalfHeight - Linear.squeeze(-SCALE, y / SCALE, SCALE) * mHeight + YOFF, mHeight - 1);
    final int ndx = yc * mWidth + xc;
    mDensity[ndx]++;
    if (mDensity[ndx] > mMaxDensity)
      mMaxDensity = mDensity[ndx];
    return ndx;
  }
}
