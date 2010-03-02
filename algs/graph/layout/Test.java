package algs.graph.layout;

import algs.graph.*;
import java.util.Set;

class Test {

  Test() {
  }

  public void run() {
    final Graph<String> g = new AdjacencyMapGraph<String>();
    g.addEdge("a", "b");
    g.addEdge("a", "1");
    g.addEdge("a", "z");
    g.addEdge("b", "c");
    g.addEdge("b", "e");
    g.addEdge("c", "d");
    final BreadthFirstIterator<String> itr = g.breadthFirstIterator();
    String start = "a";
    itr.setStart(start);
    Set<String> neigh = g.getNeighbors(start);
    while (itr.hasNext()) {
      final String cur = itr.next();
      System.out.println(cur);
    }
  }

  public static void main(final String [] args) {
    new Test().run();
  }
}