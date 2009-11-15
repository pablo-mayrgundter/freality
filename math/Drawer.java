package math;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

class Drawer {

  final int mScreenWidth, mScreenHeight, mDrawWidth, mDrawHeight;
  final boolean mOversample;
  BufferedImage mImg = null;

  Drawer(final int screenWidth, final int screenHeight,
         final int overSample) {
    mScreenWidth = screenWidth;
    mScreenHeight = screenHeight;
    mDrawWidth = mScreenWidth * overSample;
    mDrawHeight = mScreenHeight * overSample;
    mOversample = overSample > 1;
    mImg = new BufferedImage(mDrawWidth, mDrawHeight, BufferedImage.TYPE_INT_ARGB);
    mImg.setAccelerationPriority(1);
  }

  public void clear() {
    Graphics g = mImg.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, mDrawWidth, mDrawHeight);
  }

  Image render(final math.complexity.Grapher grapher) {
    grapher.toImage(mImg);
    if (mOversample) {
      return mImg.getScaledInstance(mScreenWidth, mScreenHeight, BufferedImage.SCALE_SMOOTH);
    }
    return mImg;
  }
}
