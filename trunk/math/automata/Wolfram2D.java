package math.automata;

import gfx.TextGraphics;
import math.automata.Wolfram;
import util.*;

final class Wolfram2D {

  final int mWidth;
  final int mHeight;
  final TextGraphics mGraphics;

  BitBuffer mCur, mNext;

  Wolfram2D (final int width, final int height) {
    this(width, height, new TextGraphics(width, height));
  }

  Wolfram2D (final int width, final int height, final TextGraphics g) {
    mWidth = width;
    mHeight = height;
    mCur = new BitBuffer(width);
    mNext = new BitBuffer(width);
    mGraphics = g;
  }

  public void renderRule (final byte rule) {
    mCur.clear().set(mWidth/2, 1);

    BitBuffer tmp;
    for (int y = 0; y < mHeight - 1; y++) {
      for (int x = 0; x < mWidth; x++) {
        if (mCur.get(x) != 0) {
          mGraphics.drawPixel(x, y);
        }
      }
      Wolfram.apply(mCur, mNext, rule);
      tmp = mCur; mCur = mNext; mNext = tmp;
      mNext.clear();
    }
  }

  public static void main (final String [] args) {
    final int from = Integer.parseInt(System.getProperty("from", "0"));
    final int to = Integer.parseInt(System.getProperty("to", "255"));
    final Wolfram2D w = new Wolfram2D(Integer.parseInt(System.getProperty("width", "80")),
                                      Integer.parseInt(System.getProperty("height", "20")));

    for (int rule = from; rule <= to; rule++) {
      w.renderRule((byte) rule);
      try {
        Thread.sleep(500);
      } catch (Exception e) {
      }
    }
  }
}
