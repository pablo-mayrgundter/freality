package math.automata.viz;

import gfx.TextGraphics;
import util.Bits;
import java.util.List;


public final class Drawer {

  final List<Bits> mBufs;
  final TextGraphics mGraphics;

  public Drawer(final List<Bits> bufs) {
    mBufs = bufs;
    mGraphics = new TextGraphics(bufs.get(0).getLength(), bufs.size());
  }

  public void draw(final int depth) {
    mGraphics.clear();
    mGraphics.setBackground(java.awt.Color.BLUE);
    for (int i = 0; i < depth; i++)
      drawBits(i, mBufs.get(i));
    mGraphics.setBackground(java.awt.Color.WHITE);
  }

  public String drawBits(final int row, final Bits b) {
    String s = "";
    for (int j = 0, n = b.getLength(); j < n; j++) {
      final int bit = b.get(j);
      if (bit != 0) {
        mGraphics.drawPixel(j, row);
      }
    }
    return s;
  }
  /*
  public String drawBitsWithRules(final Bits b, final byte [] rules) {
    return drawBitsWithRules(b, rules, "% 4d");
  }

  public String drawBitsWithRules(final Bits b, final byte [] rules, final String format) {
    String s = "";
    for (int j = 0, n = b.getLength(); j < n; j++)
      s += (b.get(j) == 1 ? SET : NOT) + String.format(format, rules[j] & 0xFF);
    return s + NOT;
    }*/
}
