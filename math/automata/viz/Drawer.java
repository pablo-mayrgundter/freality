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

  public void clear() {
    mGraphics.clear();
  }

  public void draw(final int depth) {
    mGraphics.setBackground(java.awt.Color.BLUE);
    for (int i = 0; i < depth + 1; i++)
      drawBits(mBufs.get(i), i);
    mGraphics.setBackground(java.awt.Color.WHITE);
    System.out.println();
  }

  public String drawBits(final Bits b, final int rowNum) {
    String s = "";
    for (int j = 0, n = b.getLength(); j < n; j++) {
      final int bit = b.get(j);
      if (bit != 0) {
        mGraphics.drawPixel(j, 1 + rowNum);
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
