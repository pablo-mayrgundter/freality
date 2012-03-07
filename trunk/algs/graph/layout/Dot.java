package algs.graph.layout;

import algs.graph.AdjacencyMapGraph;
import algs.graph.BreadthFirstIterator;
import algs.graph.Graph;
import java.util.Scanner;
import java.util.regex.*;

class Dot {

  static final Pattern EDGE = Pattern.compile("\\s*(.*?)\\s*->\\s*(.*?)\\s*");

  final Graph<String> graph;

  Dot() {
    graph = new AdjacencyMapGraph<String>();
  }

  public void run() {
    final Scanner s = new Scanner(System.in);
    final Matcher m = EDGE.matcher("");
    while (s.hasNext()) {
      final String line = s.nextLine();
      m.reset(line);
      if (m.matches())
        graph.addEdge(m.group(1), m.group(2));
    }
    for (final String node : graph.nodes())
      for (final String neighbor : graph.getNeighbors(node))
        System.out.println(node +" -> "+ neighbor);
    final BreadthFirstIterator<String> bfs = graph.bfs();
    bfs.setStart("Pablo");
    while (bfs.hasNext())
      System.out.println(bfs.next());
  }
  public static void main(final String [] args) {
    new Dot().run();
  }
}