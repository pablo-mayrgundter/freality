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
class Graph<T> {

    final Map<T,Set<T>> links;

    Graph() {
        links = new HashMap<T,Set<T>>();
    }

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
        Set<T> neighbors = links.get(node1);
        if (neighbors == null) {
            neighbors = new HashSet<T>();
            links.put(node1, neighbors);
        }
        neighbors.add(node2);
    }

    Set<T> nodes() {
        return links.keySet();
    }

    Set<T> neighbors(T node) {
        return links.get(node);
    }

    /**
     * Test a String graph.
     */
    public static void main(String [] args) {
        final String [] mappings = args[0].split("\\|");
        final Graph<String> g = new Graph<String>();
        for (String mapping : mappings) {
            final String [] nodeLinks = mapping.split(":");
            final String node = nodeLinks[0];
            final String [] neighbors = nodeLinks[1].split(",");
            for (String neighbor : neighbors)
                g.link(node, neighbor);
        }

        for (String node : g.nodes())
            System.out.println(node + ": " + g.neighbors(node));
    }
}
