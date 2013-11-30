package phys;

import unit.TestSuite;

public class AllTests {

  public static TestSuite suite() {
    final TestSuite suite = new TestSuite();
    suite.addTest(SpaceTest.class);
    suite.addTest(Space1DBinaryRingTest.class);
    suite.addTest(TimeSpace1DBinaryRingTest.class);
    return suite;
  }

  public static void main(final String [] args) {
    suite().run().println();
  }
}
