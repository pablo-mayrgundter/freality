package math.complexity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class Grapher extends ColoredDensityMap {

  public Grapher(final int width, final int height) {
    super(width, height);
  }

  public void toImage(final BufferedImage img) {
    float a = 0;
    float mMax = (float) mMaxDensity;
    float lMax = (float) Math.log(mMaxDensity);
    final java.awt.Graphics g = img.getGraphics();
    g.setColor(java.awt.Color.BLACK);

    int lX = 0, lY = 0;
    for (int y = 0, H = mHeight - 1; y < H; y++) {
      final int ndxOffset = y * mWidth;
      for (int x = 1, W = mWidth - 1; x < W; x++) {
        final int ndx = ndxOffset + x;
        final int density = mDensity[ndx];
        if (density < 1)
          continue;

        final float lD = (float) Math.log(density);
        final float normalized = lD/lMax * 255f;
        //        a = (float) density / mMax;
        //        a = normalized * 255f;
        //        a = ((float) Math.pow(normalized, .75)) * 4f;
        a = ((float) Math.pow(normalized, .454545)) * 22f;
        //        a = ((float) Math.pow(normalized, .5)) * 50f;
        //        a = ((float) Math.pow(lD/lMax, .5)) * 255f;
        //        System.out.println(a);
        assert a >= 0 && a <= 255f : "a out of range: " + a;
        final int al = (((int) a) << 24);
        final int pc = 0x00ffffff & mPalette[(int) (mColor[ndx] * (mPalette.length - 1))];
        //        img.setRGB(x, y, al | pc);
        //        if (x > lX)
        //          g.drawRect(lX, lY, x - lX, y - lY);
        //        else
        //          g.drawRect(x, y, lX - x, lY - y);
        g.drawRect(x, y, 100, 100);
        lX = x;
        lY = y;
      }
    }
  }
}
