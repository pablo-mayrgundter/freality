package physics;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class TimeSpace1DBinaryRingTest extends Space1DBinaryRingTest {

    public TimeSpace1DBinaryRingTest (final String name) {
        super(name);
    }

    public void testInitializedToZero() {
        final TimeSpace1DBinaryRing space = new TimeSpace1DBinaryRing(10);
        for (int i = 0, n = space.getExtent(); i < n; i++)
            space.setNext(i, 1);
        space.nextTime();
        for (int i = 0, n = space.getExtent(); i < n; i++)
            assertEquals("Expected time+1 value to be 1 at index: "+ i, 1, space.get(i));
    }

    public static TestSuite suite () {
        final TestSuite suite = new TestSuite();
        suite.addTestSuite(TimeSpace1DBinaryRingTest.class);
        return suite;
    }

    public static void main (final String [] args) {
        junit.textui.TestRunner r = new junit.textui.TestRunner();
        r.run(suite());
    }
}
