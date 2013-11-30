package util;

import unit.TestCase;

/**
 * Tests for the BitBuffer class.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class BitBufferTest extends TestCase {

  public void testGet() {
    for (int i = 0; i < 17; i++)
      testGet(new BitBuffer(i));
  }

  public void testSet() {
    for (int i = 0; i < 17; i++)
      testSet(new BitBuffer(i));
    final BitBuffer b = new BitBuffer(9);
    b.set(0,1);
    b.set(3,1);
    b.set(8,1);
    assertEquals(1, b.get(0));
    assertEquals(0, b.get(1));
    assertEquals(0, b.get(2));
    assertEquals(1, b.get(3));
    assertEquals(0, b.get(4));
    assertEquals(0, b.get(5));
    assertEquals(0, b.get(6));
    assertEquals(0, b.get(7));
    assertEquals(1, b.get(8));
  }

  void testGet(final BitBuffer bits) {
    for (int i = bits.position(), n = bits.limit(); i < n; i++) {
      assertEquals(0, bits.get(i), "Expected bit to be 0 at index: %d of %d", i, n);
    }
  }

  void testSet(final BitBuffer bits) {
    for (int i = bits.position(), n = bits.limit(); i < n; i++) {
      bits.set(i, 1);
      assertEquals(1, bits.get(i), "Expected bit to be 1 at index: %d of %d", i, n);
      bits.set(i, 0);
      assertEquals(0, bits.get(i), "Expected bit to be 0 at index: %d of %d", i, n);
    }
  }

  public static void main(final String [] args) {
    new BitBufferTest().run().println();
  }
}
