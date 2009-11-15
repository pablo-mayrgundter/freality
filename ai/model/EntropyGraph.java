package ai.model;

/**
 * An EntropyGraph extends a FrequencyGraph by including methods for
 * computing inter-node entropy.  Entropy is defined here as:
 *
 *   -log(probability(node1, node2))
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
public class EntropyGraph<T> extends FrequencyGraph<T> {

    double mMaxE;
    T mMaxNode;

    public EntropyGraph() {
        mMaxE = Double.MAX_VALUE;
        mMaxNode = null;
    }

    double entropy(T node1, T node2) {
        return -(Math.log(probability(node1, node2))/Math.log(2.0)); // FIXME: This right?.
    }

    public void linkDirectional(T node1, T node2) {
        super.linkDirectional(node1, node2);
        final double e = entropy(node1, node2);
        if (e < mMaxE) {
            mMaxE = e;
            mMaxNode = node1;
        }
    }

    /**
     * @return The node which, along with its max neighbor, represents
     * the highest entropy link in the graph, or null if graph is
     * empty.
     */
    public T maxEntropyNode() {
        return mMaxNode;
    }

    /**
     * @return The node which is linked with the highest entropy to
     * the given node, or null if this node has no neighbors.
     */
    public T highestEntropyNeighbor(T node) {
        double maxE = Double.MAX_VALUE;
        T maxN = null;
        for (T neighbor : neighbors(node)) {
            final double e = entropy(node, neighbor);
            if (e < maxE) {
                maxE = e;
                maxN = neighbor;
            }
        }
        return maxN;
    }

    public static void main(String [] args) {
        final String [] mappings = args[0].split("\\|");
        final EntropyGraph<String> g = new EntropyGraph<String>();
        for (String mapping : mappings) {
            final String [] nodeLinks = mapping.split(":");
            final String node = nodeLinks[0];
            final String [] neighbors = nodeLinks[1].split(",");
            for (String neighbor : neighbors)
                g.link(node, neighbor);
        }

        for (String node : g.nodes())
            for (String neighbor : g.neighbors(node))
                System.out.println(node + "->" + neighbor + ": " + g.entropy(node, neighbor));
    }
}
