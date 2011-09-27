package math.complexity;

import java.awt.Color;
import java.util.Random;

/**
 * The ColoredDensityMap class extends DensityMap to also record a
 * color component of a density.
 *
 * @author Pablo Mayrgundter
 */
class ColoredDensityMap extends DensityMap {

  final float [] mColor;
  final Random mRand;
  int [] mPalette;

  ColoredDensityMap(final int width, final int height, final int scale) {
    super(width, height, scale);
    mRand = new Random();
    mColor = new float[mWidth * mHeight];
    mPalette = new int[10];
    System.out.println("Number of colors in palette: " + mPalette.length);
    init();
  }

  public void setSeed(long seed) {
    mRand.setSeed(seed);
  }

  public void init() {
    for (int i = 0; i < mPalette.length; i++) {
      mPalette[i] = new Color(mRand.nextFloat(), mRand.nextFloat(), mRand.nextFloat()).getRGB();
      //mPalette[i] = Color.BLACK.getRGB();
    }
  }

  public void clear() {
    super.clear();
    java.util.Arrays.fill(mColor, 0);
  }

  public void map(final double x, final double y, final float funcNdx) {
    //    System.out.println(funcNdx);
    final int ndx = super.map(x, y);
    // Move some of this normalization to the draw() routine ?
    float c = (mColor[ndx] + funcNdx / (float) mPalette.length) / 2f;
    mColor[ndx] = (float) math.util.Linear.squeeze(0, c, 1);
    assert mColor[ndx] >= 0 && mColor[ndx] <= 1f : "mColor[ndx]: "+ mColor[ndx];
  }

  static final long serialVersionUID = -40350624628237367L;
}
