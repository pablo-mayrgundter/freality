package algs.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The AdjacencyMapGraph class is an adjacency map representation of a graph.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
public class AdjacencyMapGraph<T> implements Graph<T> {

  final Map<T,Set<T>> edges;

  public AdjacencyMapGraph() {
    edges = new HashMap<T,Set<T>>();
  }

  /**
   * Equivalent to calling addEdge(node, null).
   */
  public void addNode(final T node) {
    addEdge(node, null);
  }

  /**
   * @return True iff the given node has been added to the graph via addNode.
   */
  public boolean hasNode(final T node) {
    return edges.get(node) != null;
  }

  /**
   * Add an adjacency mapping from node a to b, creating the neighbor
   * set if needed.  If the second node is null, the neighbor set will
   * not be affected.
   *
   * @param a Node reference, may not be null.
   * @param b Node reference, may be null.
   */
  public void addEdge(final T a, final T b) {
    if (a == null)
      throw new NullPointerException("First node may not be null.");
    Set<T> neighbors = edges.get(a);
    if (neighbors == null)
      edges.put(a, neighbors = new HashSet<T>());
    neighbors.add(b);
  }

  /**
   * @return True iff there exists an edge from a to b.
   */
  public boolean hasEdge(final T a, final T b) {
    final Set<T> neighbors = edges.get(a);
    return neighbors != null && neighbors.contains(b);
  }

  public Set<T> nodes() {
    return edges.keySet();
  }

  public Set<T> getNeighbors(final T node) {
    return edges.get(node);
  }

  public Iterator<T> bfs() {
    return new BreadthFirstIterator<T>(this);
  }

  public String toString() {
    String s = "";
    for (final T node : nodes())
      s += node + ": " + getNeighbors(node) + "\n";
    return s;
  }

  /**
   * Test a String graph.
   */
  public static void main(String [] args) {
    final String [] mappings = args[0].split("\\|");
    final Graph<String> g = new AdjacencyMapGraph<String>();
    for (String mapping : mappings) {
      final String [] nodeEdges = mapping.split(":");
      final String node = nodeEdges[0];
      final String [] neighbors = nodeEdges[1].split(",");
      for (String neighbor : neighbors)
        g.addEdge(node, neighbor);
    }
    System.out.println(g);
  }
}
