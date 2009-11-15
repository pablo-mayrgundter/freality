package phys;

import junit.framework.TestSuite;

public class AllTests {

  public static TestSuite suite () {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(Space1DBinaryRingTest.class);
    suite.addTestSuite(TimeSpace1DBinaryRingTest.class);
    return suite;
  }

  public static void main (final String [] args) {
    junit.textui.TestRunner r = new junit.textui.TestRunner();
    r.run(suite());
  }
}
