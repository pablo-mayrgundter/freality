package math.complexity;

import unit.TestCase;

/**
 * The DensityMapTest class covers the cartesian to screen space
 * conversion.
 *
 * @author Pablo Mayrgundter
 */
public class DensityMapTest extends TestCase {

  DensityMap map;

  public void setUp() {
    map = new DensityMap(10, 10, 17);
  }

  public void tearDown() {
    map = null;
  }

  public void testMap() {
    map.map(3, 4.2);
    map.map(3, 4.2);
    map.map(3, 3.2);
    assertEquals(2, map.getDensity(3, 4.2));
    assertEquals(1, map.getDensity(3, 3.2));
    assertEquals(0, map.getDensity(3, 8.2));
    assertEquals(2, map.getMaxDensity());
  }

  /**
   * Runnable as:
   *
   *   java math.algs.DensityMapTest
   */
  public static void main(String [] args) {
    new DensityMapTest().run().println();
  }
}