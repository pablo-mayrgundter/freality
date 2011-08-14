package ai.model;

import ai.util.MutableInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * A FrequencyGraphs adds a count for each call to link(A, B) that can
 * be retrieved with frequency(A, B).
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class FrequencyGraph<T> extends Graph<T> {

  /** An entry in the count map. */
  final class Pair<T, E> {
    final T node1;
    final E node2;
    Pair(T node1, E node2) {
      this.node1 = node1;
      this.node2 = node2;
    }

    public int hashCode() {
      return node1.hashCode() + node2.hashCode();
    }

    public boolean equals(Object o) {
      return (o instanceof Pair) ?
        ((Pair)o).node1.equals(this.node1)
        && ((Pair)o).node2.equals(this.node2)
        : false;
    }
  }

  final Map<Pair, MutableInteger> counts;
  int eventCount;

  FrequencyGraph() {
    counts = new HashMap<Pair, MutableInteger>();
    eventCount = 0;
  }

  void linkDirectional(T node1, T node2) {
    super.linkDirectional(node1, node2);
    final Pair<T, T> p = new Pair<T, T>(node1, node2);
    MutableInteger count = counts.get(p);
    if (count == null) {
      counts.put(p, count = new MutableInteger());
    }
    count.val++;
    eventCount++;
  }

  /**
   * @return The frequency of the link, zero if the link has never
   * been made.
   */
  int frequency(T node1, T node2) {
    final Pair<T, T> p = new Pair<T, T>(node1, node2);
    MutableInteger count = counts.get(p);
    if (count == null) {
      return 0;
    }
    return count.val;
  }

  /**
   * @return The probability of the link, which is defined as the
   * frequency / event count.
   */
  float probability(T node1, T node2) {
    return (float) frequency(node1, node2) / (float) eventCount;
  }

  public static void main(String [] args) {
    final String [] mappings = args[0].split("\\|");
    final FrequencyGraph<String> g = new FrequencyGraph<String>();
    for (String mapping : mappings) {
      final String [] nodeLinks = mapping.split(":");
      final String node = nodeLinks[0];
      final String [] neighbors = nodeLinks[1].split(",");
      for (String neighbor : neighbors) {
        g.link(node, neighbor);
      }
    }

    for (String node : g.nodes()) {
      for (String neighbor : g.neighbors(node)) {
        System.out.println(node + "->" + neighbor + ": " + g.frequency(node, neighbor));
      }
    }
  }
}
