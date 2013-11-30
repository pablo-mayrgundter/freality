package algs.search;

import unit.TestSuite;

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
    suite.addTest(IndexTest.class);
    return suite;
  }

  /**
   * Runnable as:
   *
   *   java algs.search.AllTests
   */
  public static void main(final String [] args) {
    suite().run();
  }
}
