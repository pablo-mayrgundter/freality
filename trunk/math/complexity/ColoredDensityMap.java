package math.complexity;

import java.awt.Color;

class ColoredDensityMap extends DensityMap {

  final float [] mColor;
  int [] mPalette;

  ColoredDensityMap(final int width, final int height) {
    super(width, height);
    mColor = new float[mWidth * mHeight];
    mPalette = new int[10];
    initPalette();
  }

  public void clear() {
    super.clear();
    java.util.Arrays.fill(mColor, 0);
  }

  public void initPalette() {
    System.out.println("Palette length: "+ mPalette.length);
    for (int i = 0; i < mPalette.length; i++)
      //      mPalette[i] = new Color((float)Math.random(), (float)Math.random(), (float)Math.random()).getRGB();
      mPalette[i] = Color.BLACK.getRGB();
  }

  public void map(final double x, final double y, final float funcNdx) {
    //    System.out.println(funcNdx);
    final int ndx = super.map(x, y);
    // Move some of this normalization to the draw() routine ?
    float c = (mColor[ndx] + funcNdx / (float) mPalette.length) / 2f;
    mColor[ndx] = (float) math.util.Linear.squeeze(0, c, 1);
    assert mColor[ndx] >= 0 && mColor[ndx] <= 1f : "mColor[ndx]: "+ mColor[ndx];
  }
}
