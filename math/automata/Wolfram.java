package math.automata;

import util.BitBuffer;
import util.Bits;

/**
 * The Wolfram class implements the binary automata described by
 * Stephen Wolfram.
 * 
 * rule   "158" out of 256 possible
 *           |
 *           v
 * 0: 000 -> 0 1
 * 1: 001 -> 1 2
 * 2: 010 -> 1 4
 * 3: 011 -> 1 8
 * 4: 100 -> 1 16
 * 5: 101 -> 0 32
 * 6: 110 -> 0 64
 * 7: 111 -> 1 128
 * 
 * 000 001 010 011 100 101 110 111
 *  1   1   1   1   1   0   0   1
 * 
 * 1*2^0 + 1*2^1 + 1*2^2 + 1*2^3 + 0*2^4 + 0*2^5 + 0*2^6 + 1*2^7 = 158
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
public class Wolfram {

  /**
   * Applies the given rule to the input buffer, setting the result
   * in the destination buffer.
   */
  public static void apply(final BitBuffer src, final BitBuffer dst, final byte rule) {
    int l, c, r, from = src.position();
    for (int i = from, to = src.limit() - 1; i <= to; i++) {
      l = src.get(i == from ? to : (i - 1)) * 4;
      c = src.get(i) * 2;
      r = src.get(i == to ? from : (i + 1));
      dst.set(i, Bits.get(rule, l + c + r));
    }
  }

  public static void apply(final Bits src, final int srcNdx,
                           final Bits dst, final int dstNdx,
                           final byte rule) {
    final int l = srcNdx == 0 ? src.get(src.getLength() - 1) : src.get(srcNdx - 1);
    final int c = src.get(srcNdx);
    final int r = srcNdx == src.getLength() - 1 ? src.get(0) : src.get(srcNdx + 1);
    dst.set(dstNdx, Bits.get(rule, l * 4 + c * 2 + r));
  }
}
