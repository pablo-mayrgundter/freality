package math.automata.viz;

import gfx.vt.VT100;
import util.Bits;
import java.util.List;

public final class Drawer {

  final List<Bits> mBufs;

  public Drawer (final List<Bits> bufs) {
    mBufs = bufs;
  }

  public void draw (final int depth) {
    System.out.print(VT100.CURSOR_HOME);
    for (int i = 0; i < depth; i++)
      System.out.println(drawBits(mBufs.get(i)));
    System.out.print(VT100.BG_BLACK);
  }

  public String drawBits (final Bits b) {
    String s = "";
    for (int j = 0, n = b.getLength(); j < n; j++)
      s += ((b.get(j) == 1 ? VT100.BG_WHITE : VT100.BG_BLACK) + " ");
    return s;
  }

  static final String SET = VT100.BG_BLUE, NOT = VT100.BG_BLACK;
  public String drawBitsWithRules (final Bits b, final byte [] rules) {
    return drawBitsWithRules(b, rules, "% 4d");
  }

  public String drawBitsWithRules (final Bits b, final byte [] rules, final String format) {
    String s = "";
    for (int j = 0, n = b.getLength(); j < n; j++)
      s += (b.get(j) == 1 ? SET : NOT) + String.format(format, rules[j] & 0xFF);
    return s + NOT;
  }

  public void clear() {
    System.out.print(VT100.CLEAR_SCREEN);
    System.out.print(VT100.CURSOR_HOME);
    System.out.print(VT100.BG_BLACK);
  }
}
