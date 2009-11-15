package util;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the BitBuffer class.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class BitBufferTest extends TestCase {

  public BitBufferTest (final String name) {
    super(name);
  }

  public void testGet () {
    for (int i = 0; i < 17; i++)
      testGet(new BitBuffer(i));
  }

  public void testSet () {
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

  void testGet (final BitBuffer bits) {
    for (int i = bits.position(), n = bits.limit(); i < n; i++)
      assertEquals("Expected bit to be 0 at index: "+ i +" of "+ n, 0, bits.get(i));
  }

  void testSet (final BitBuffer bits) {
    for (int i = bits.position(), n = bits.limit(); i < n; i++) {
      bits.set(i, 1);
      assertEquals("Expected bit to be 1 at index: "+ i +" of "+ n, 1, bits.get(i));
      bits.set(i, 0);
      assertEquals("Expected bit to be 0 at index: "+ i +" of "+ n, 0, bits.get(i));
    }
  }

  public static TestSuite suite () {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(BitBufferTest.class);
    return suite;
  }

  public static void main (final String [] args) {
    junit.textui.TestRunner r = new junit.textui.TestRunner();
    r.run(suite());
  }
}
