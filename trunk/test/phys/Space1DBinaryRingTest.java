package phys;

import unit.TestCase;

public class Space1DBinaryRingTest extends TestCase {

  public void testInitializedToZero() {
    final Space1DBinaryRing space = new Space1DBinaryRing(10);
    for (int i = 0, n = space.getExtent(); i < n; i++) {
      assertEquals("Expected space to be initialized to 0 at index: "+ i, 0, space.get(i));
    }
  }
}
