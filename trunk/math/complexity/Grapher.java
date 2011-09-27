package math.complexity;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * The Grapher class extends ColoredDensityMap to enable drawing the
 * density map to an image for display.
 *
 * @author Pablo Mayrgundter
 */
public final class Grapher extends ColoredDensityMap {

  public Grapher(final int width, final int height, final int scale) {
    super(width, height, scale);
  }

  public void toImage(final BufferedImage img) {
    if (Boolean.getBoolean("mono")) {
      toMonoImage(img);
    } else {
      toColorImage(img);
    }
  }

  void toMonoImage(BufferedImage img) {
    float mMax = (float) mMaxDensity;
    float lMax = (float) Math.log(mMaxDensity);
    final java.awt.Graphics g = img.getGraphics();
    g.setColor(Color.BLACK);

    for (int y = 0, H = mHeight - 1; y < H; y++) {
      final int ndxOffset = y * mWidth;
      for (int x = 1, W = mWidth - 1; x < W; x++) {
        final int ndx = ndxOffset + x;
        final int density = mDensity[ndx];
        if (density < 1) {
          continue;
        }

        final float lD = (float) Math.log(density);
        final float normalized = 255f;
        final int alpha = (((int) normalized) << 24);
        img.setRGB(x, y, alpha);
      }
    }
  }

  void toColorImage(BufferedImage img) {
    float a = 0;
    float mMax = (float) mMaxDensity;
    float lMax = (float) Math.log(mMaxDensity);
    final java.awt.Graphics g = img.getGraphics();
    g.setColor(Color.BLACK);

    int lX = 0, lY = 0;
    for (int y = 0, H = mHeight - 1; y < H; y++) {
      final int ndxOffset = y * mWidth;
      for (int x = 1, W = mWidth - 1; x < W; x++) {
        final int ndx = ndxOffset + x;
        final int density = mDensity[ndx];
        if (density < 1) {
          continue;
        }

        final float lD = (float) Math.log(density);
        //final float normalized = lD/lMax * 255f;

        //a = (float) density / mMax;
        //a = normalized * 255f;
        //a = ((float) Math.pow(normalized, .75)) * 4f;
        //a = ((float) Math.pow(normalized, .454545)) * 22f;
        //a = ((float) Math.pow(normalized, .5)) * 50f;
        a = ((float) Math.pow(lD/lMax, 0.5)) * 255f;
        //assert a >= 0 && a <= 255f : "a out of range: " + a;
        final int al = (((int) a) << 24);
        final int pc = 0x00ffffff & mPalette[(int) (mColor[ndx] * (mPalette.length - 1))];
        img.setRGB(x, y, al | pc);
      }
    }
  }

  static final long serialVersionUID = -5327635227869241592L;
}
