package algs.search;

import junit.framework.TestSuite;

/**
 * This is a convenient target for the build system to invoke all of
 * the unit tests.
 *
 * @author Pablo Mayrgundter
 */
public class AllTests {

  /**
   * Creates a suite of all unit tests in this package and
   * sub-packages.
   */
  public static TestSuite suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(IndexTest.class);
    return suite;
  }

  /**
   * Runnable as:
   *
   *   java algs.search.AllTests
   */
  public static void main(final String [] args) {
    junit.textui.TestRunner.run(suite());
  }
}
