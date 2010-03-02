package algs.graph;

import java.util.Set;

/**
 * The Graph interface defines generic graph semantics independent of
 * implementation.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
public interface Graph<T> {
  void addNode(T a);
  boolean hasNode(T a);
  void addEdge(T a, T b);
  boolean hasEdge(T a, T b);
  Set<T> nodes();
  Set<T> getNeighbors(final T node);
  BreadthFirstIterator<T> breadthFirstIterator();
}
