package phys;

/**
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class TimeSpace1DBinaryRingTest extends Space1DBinaryRingTest {

  public void testInitializedToZero() {
    final TimeSpace1DBinaryRing space = new TimeSpace1DBinaryRing(10);
    for (int i = 0, n = space.getExtent(); i < n; i++) {
      space.setNext(i, 1);
    }
    space.nextTime();
    for (int i = 0, n = space.getExtent(); i < n; i++) {
      assertEquals(1, space.get(i), "Expected time+1 value to be 1 at index: " + i);
    }
  }
}
