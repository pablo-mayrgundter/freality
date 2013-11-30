package phys;

import unit.TestCase;

/**
 * Tests for the Space class.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
public class SpaceTest extends TestCase {

  public void testCoordToNdx() {
    assertEquals(0, Space.coordToNdx(1, 0));
    assertEquals(1, Space.coordToNdx(2, 1));
    assertEquals(0, Space.coordToNdx(2, 0, 0));
    assertEquals(1, Space.coordToNdx(2, 1, 0));
    assertEquals(2, Space.coordToNdx(2, 0, 1));
    assertEquals(3, Space.coordToNdx(2, 1, 1));
    assertEquals(4, Space.coordToNdx(2, 0, 0, 1));
  }

  public void testSpace1D() {
    int dimension = 1;
    int radius = 3;
    Space s = new Space(dimension, radius);
    int x1 = 1;
    int val = 5;
    s.set(val, x1);
    for (int x = 0; x < radius; x++) {
      int expect = x == x1 ? val : 0;
      assertEquals(expect, s.get(x), "expect: space[%d] == %d", x, expect);
    }
  }

  public void testSpace2D() {
    int dimension = 2;
    int radius = 3;
    Space s = new Space(dimension, radius);
    int x1 = 1, x2 = 1;
    int val = 5;
    s.set(val, x1, x2);
    for (int x = 0; x < radius; x++) {
      for (int y = 0; y < radius; y++) {
        int expect = x == x1 && y == x2 ? val : 0;
        assertEquals(expect, s.get(x, y), "expect: space[%d, %d] == %d", x, y, expect);
      }
    }
  }
}
