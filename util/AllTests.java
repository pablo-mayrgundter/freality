package util;

import junit.framework.TestSuite;

/**
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class AllTests {

  public static TestSuite suite () {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(BitsTest.class);
    suite.addTestSuite(BitBufferTest.class);
    return suite;
  }

  public static void main (final String [] args) {
    junit.textui.TestRunner r = new junit.textui.TestRunner();
    r.run(suite());
  }
}
