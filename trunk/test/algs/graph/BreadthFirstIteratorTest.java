package algs.graph;

import unit.TestCase;

public class BreadthFirstIteratorTest extends TestCase {

  Graph<String> g;
  BreadthFirstIterator<String> itr;

  public void setUp() {
    g = new AdjacencyMapGraph<String>();
    itr = new BreadthFirstIterator<String>(g);
  }

  public void tearDown() {
    g = null; itr = null;
  }

  public void testSingleNode() {
    g.addNode("a");
    itr.setStart("a");
    assertTrue(itr.hasNext());
    assertEquals("a", itr.next());
    assertFalse(itr.hasNext());
    assertEquals(null, itr.next());
  }

  public void testSingleEdge() {
    g.addEdge("a", "b");
    itr.setStart("a");
    assertTrue(itr.hasNext());
    assertEquals("a", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("b", itr.next());
    assertFalse(itr.hasNext());
    assertEquals(null, itr.next());
  }

  public void testVee() {
    g.addEdge("a", "b");
    g.addEdge("b", "c");
    itr.setStart("a");
    assertTrue(itr.hasNext());
    assertEquals("a", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("b", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("c", itr.next());
    assertFalse(itr.hasNext());
    assertEquals(null, itr.next());
  }

  public void testTriangle() {
    g.addEdge("a", "b");
    g.addEdge("b", "c");
    g.addEdge("c", "a");
    itr.setStart("a");
    assertTrue(itr.hasNext());
    assertEquals("a", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("b", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("c", itr.next());
    assertFalse(itr.hasNext());
    assertEquals(null, itr.next());
  }

  public void testTriangleWithSpur() {
    g.addEdge("a", "b");
    g.addEdge("b", "c");
    g.addEdge("c", "a");
    g.addEdge("c", "d");
    itr.setStart("a");
    assertTrue(itr.hasNext());
    assertEquals("a", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("b", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("c", itr.next());
    assertTrue(itr.hasNext());
    assertEquals("d", itr.next());
    assertFalse(itr.hasNext());
    assertEquals(null, itr.next());
  }
}
