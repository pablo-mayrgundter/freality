package util;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Bits class.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class BitsTest extends TestCase {

    public BitsTest (final String name) {
        super(name);
    }

    public void testGet () {
        for (int i = 0; i < 17; i++) {
            final Bits bits = new Bits(i);
            testGet(bits, 0);
        }
    }

    public void testSet () {
        for (int i = 0; i < 17; i++) {
            final Bits bits = new Bits(i);
            testSet(bits, 1);
        }
    }

    public void testFillAndClear () {
        for (int i = 0; i < 17; i++) {
            final Bits bits = new Bits(i);
            testSet(bits, 1);
            bits.clear();
            testGet(bits, 0);
            bits.fill((byte) 1);
            testGet(bits, 1);
        }
    }

  public void testSet2 () {
    final int len = 143;
    final int [] arr = new int[len];
    for (int i = 0; i < arr.length; i++)
      if (Math.random() < 0.24)
        arr[i] = 1;
    final Bits bits = new Bits(len);
    for (int i = 0; i < arr.length; i++)
      if (arr[i] == 1)
        bits.set(i);
    for (int i = 0; i < arr.length; i++)
      if (arr[i] == 1)
        assertEquals(1, bits.get(i));
  }

    void testGet(final Bits bits, final int value) {
        for (int i = 0, n = bits.getLength(); i < n; i++)
            assertEquals("Expected bit to be "+ value +" at index: "+ i +" of "+ n, value, bits.get(i));
    }

    void testSet(final Bits bits, final int value) {
        for (int i = 0, n = bits.getLength(); i < n; i++)
            bits.set(i, value);
        for (int i = 0, n = bits.getLength(); i < n; i++)
            assertEquals("Expected bit to be "+ value +" at index: "+ i +" of "+ n, value, bits.get(i));
    }

    public static TestSuite suite () {
        final TestSuite suite = new TestSuite();
        suite.addTestSuite(BitsTest.class);
        return suite;
    }

    public static void main (final String [] args) {
        junit.textui.TestRunner r = new junit.textui.TestRunner();
        r.run(suite());
    }
}
