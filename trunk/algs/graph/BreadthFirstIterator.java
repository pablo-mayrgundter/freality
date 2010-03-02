package algs.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Pablo Mayrgundter
 */
public class BreadthFirstIterator<T> implements Iterator<T> {

  final Graph<T> graph;
  final Set<T> visited;
  final LinkedList<T> upcoming;
  T next = null;

  public BreadthFirstIterator(final Graph<T> graph) {
    this.graph = graph;
    visited = new HashSet<T>();
    upcoming = new LinkedList<T>();
  }

  public void setStart(final T start) {
    if (!graph.hasNode(start))
      throw new IllegalArgumentException("Given node not in graph.");
    setNext(start);
  }

  public boolean hasNext() {
    return next != null || upcoming.size() > 0;
  }

  public T next() {
    // Add current node to visited and return it.  If upcoming is not
    // empty, set cur to the popped head and add its neighbors to
    // upcoming if they're neither in the visited or upcoming
    // collections.
    final T ret = next;
    visited.add(ret);
    if (upcoming.size() > 0)
      setNext(upcoming.removeFirst());
    else
      setNext(null);
    return ret;
  }

  public void remove() {
    throw new UnsupportedOperationException("Method not implemented.");
  }

  void setNext(final T node) {
    next = node;
    final Set<T> neighbors = graph.getNeighbors(next);
    if (neighbors == null)
      return;
    for (final T n : neighbors) {
      if (visited.contains(n) || upcoming.contains(n))
        continue;
      upcoming.add(n);
    }
  }
}