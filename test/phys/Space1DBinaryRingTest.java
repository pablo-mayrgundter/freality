package phys;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class Space1DBinaryRingTest extends TestCase {

  public Space1DBinaryRingTest (final String name) {
    super(name);
  }

  public void testInitializedToZero() {
    final Space1DBinaryRing space = new Space1DBinaryRing(10);
    for (int i = 0, n = space.getExtent(); i < n; i++)
      assertEquals("Expected space to be initialized to 0 at index: "+ i, 0, space.get(i));
  }

  public static TestSuite suite () {
    final TestSuite suite = new TestSuite();
    suite.addTestSuite(Space1DBinaryRingTest.class);
    return suite;
  }

  public static void main (final String [] args) {
    junit.textui.TestRunner r = new junit.textui.TestRunner();
    r.run(suite());
  }
}
