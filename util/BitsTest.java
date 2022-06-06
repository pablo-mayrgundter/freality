package util;

import unit.TestCase;

/**
 * Tests for the Bits class.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
public class BitsTest extends TestCase {

  public void testGet() {
    for (int i = 0; i < 17; i++) {
      final Bits bits = new Bits(i);
      testGet(bits, 0);
    }
  }

  public void testSet() {
    for (int i = 0; i < 17; i++) {
      testSet(new Bits(i), 1);
    }
  }

  public void testFillAndClear() {
    for (int i = 0; i < 17; i++) {
      final Bits bits = new Bits(i);
      testSet(bits, 1);
      bits.clear();
      testGet(bits, 0);
      bits.fill((byte) 1);
      testGet(bits, 1);
    }
  }

  public void testSet2() {
    final int len = 143;
    final int [] arr = new int[len];
    for (int i = 0; i < arr.length; i++) {
      if (Math.random() < 0.24) {
        arr[i] = 1;
      }
    }
    final Bits bits = new Bits(len);
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == 1) {
        bits.set(i);
      }
    }
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == 1) {
        assertEquals(1, bits.get(i));
      }
    }
  }

  public void testPopCount() {
    assertEquals(0, Bits.popCount(0));
    for (int i = 0; i < 32; i++) {
      final int x = 1 << i;
      assertEquals(1, Bits.popCount(x), "Expected popCount of 1 on 1 << " + i);
    }
    for (int i = 0; i < 32; i++) {
      final int x = 1 << i;
      for (int j = 0; j < 32; j++) {
        if (i == j) {
          continue;
        }
        final int y = x | (1 << j);
        assertEquals(2, Bits.popCount(y),
                     "Expected popCount of 2 on %d == (1 << %d | 1 << %d)", y, i, j);
      }
    }
    // etc.
  }

  // Helpers.

  void testGet(final Bits bits, final int value) {
    for (int i = 0, n = bits.getLength(); i < n; i++) {
      final int actual = bits.get(i);
      assertEquals(value, actual,
                   "Expected bit to be %d at index %d of %d, got %d", value, i, n, actual);
    }
  }

  void testSet(final Bits bits, final int value) {
    for (int i = 0, n = bits.getLength(); i < n; i++) {
      bits.set(i, value);
    }
    for (int i = 0, n = bits.getLength(); i < n; i++) {
      assertEquals(value, bits.get(i),
                   "Expected bit to be %d at index %d of %d", value, i, n);
    }
  }
}
