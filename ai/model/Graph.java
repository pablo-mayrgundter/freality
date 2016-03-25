package ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Graph is a set of nodes and links between nodes.  The graph is
 * stored as a mapping of a node to a neighboring node.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class Graph<T> extends HashMap<T,Set<T>> implements Map<T,Set<T>> {

  /**
   * Creates a two-way link between the nodes, creating the nodes if
   * need be.
   */
  void link(T node1, T node2) {
    linkDirectional(node1, node2);
    linkDirectional(node2, node1);
  }

  /**
   * Creates a one-way link from the first node to the second,
   * creating the nodes if need be.
   */
  void linkDirectional(T node1, T node2) {
    Set<T> neighbors = get(node1);
    if (neighbors == null) {
      neighbors = new HashSet<T>();
      put(node1, neighbors);
    }
    neighbors.add(node2);
  }

  Set<T> nodes() {
    return keySet();
  }

  Set<T> neighbors(T node) {
    return get(node);
  }

  /**
   * Decodes strings of the form:
   *   A:B,C|B:C
   * into:
   *   A->B
   *   A->C
   *   B->C
   */
  static Graph fromString(final String encodedGraph) {
    Graph<String> g = new Graph<String>();
    fromString(encodedGraph, g);
    return g;
  }

  static Graph<String> fromString(final String encodedGraph, final Graph<String> g) {
    final String [] mappings = encodedGraph.split("\\|");
    for (String mapping : mappings) {
      final String [] nodeLinks = mapping.split(":");
      final String node = nodeLinks[0];
      final String [] neighbors = nodeLinks[1].split(",");
      for (String neighbor : neighbors) {
        g.link(node, neighbor);
      }
    }
    return g;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (T node : nodes()) {
      buf.append(String.format("%s:%s\n", node, neighbors(node)));
    }
    return buf.toString();
  }

  public static void main(String [] args, Graph<String> g) throws Exception {
    if (args.length == 0) {
      util.Streams.readLines(System.in, new util.Streams.LineFn () {
          public void apply(String line) {
            fromString(line, g);
          }
        });
    } else {
      for (String arg : args) {
        fromString(arg, g);
      }
    }
    System.out.print(g);
  }

  public static void main(String [] args) throws Exception {
    main(args, new Graph<String>());
  }
}
