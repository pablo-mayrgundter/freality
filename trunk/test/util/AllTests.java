package util;

import junit.framework.TestSuite;

/**
 * This is a convenient target for the build system to invoke all of
 * the unit tests for this package and its sub-packages.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
public class AllTests {

  /**
   * Creates a suite of all unit tests in this package and
   * sub-packages.
   */
  public static TestSuite suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(BitBufferTest.class);
    suite.addTestSuite(BitsTest.class);
    return suite;
  }

  /**
   * Runnable as:
   *
   *   java util.AllTests
   */
  public static void main(final String [] args) {
    junit.textui.TestRunner.run(suite());
  }
}
