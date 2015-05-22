package algs;

public class BDD {

  final int size;

  public BDD(int size) {
    this.size = size;
  }

  boolean member(int i, int l, int h) {
    return lookup(i, l, h) == -1;
  }

  int lookup(int i, int l, int h) {
    return -1;
  }

  int add(int i, int l, int h) {
    return -1;
  }

  void insert(int i, int l, int h, int u) {
  }

  /**
   * The function make(i, l, h) searches the BDD table for a node with
   * variable index i and low-, high-branches l; h and returns a
   * matching node if one exists.  Otherwise it creates a new node u,
   * inserts it into the BDD table and returns the identity of it. The
   * running time of make is O(1) due to the assumptions on the basic
   * operations on the BDD bookkeeping tables.  The OBDD is ensured to
   * be reduced if nodes are only created through the use of make.
   */
  public int make(int i, int l, int h) {
    if (l == h) {
      return l;
    }
    int nodeNdx = lookup(i, l, h);
    if (nodeNdx != -1) {
      return nodeNdx;
    }
    nodeNdx = add(i, l, h);
    insert(i, l, h, nodeNdx);
    return nodeNdx;
  }

  /**
   * Recursive helper for build(String).
   *
   * @param minNdx the lowest index that any variable in expr will
   * have.
   * @param maxNdx the highest index that any variable in expr will
   * have.
   */
  int build(String expr, int minNdx, int maxNdx) {
    if (minNdx > maxNdx) {
      return evalTerminalExpr(expr) ? 1 : 0;
    }
    expr = expr.substring(expr.indexOf("\\s+", 1), expr.length()).trim();
    //int v0 = build(expand(expr, ));
    return -1;
  }

  /** Builds an ROBDD from a boolean expression t using the ordering X1
   * &lt; X2 &lt; ... &lt; Xn. */
  public static BDD build(String expr) {
    int maxNdx = findLargestVarNdx(expr);
    BDD bdd = new BDD(maxNdx);
    bdd.build(expr, 1, maxNdx);
    return bdd;
  }

  static int findLargestVarNdx(String expr) {
    String [] toks = expr.split("\\s+");
    int maxNdx = 0;
    for (String tok : toks) {
      if (tok.startsWith("X")) {
        tok = tok.substring(1, tok.length());
        int ndx = Integer.parseInt(tok);
        if (ndx > maxNdx) {
          maxNdx = ndx;
        }
      }
    }
    return maxNdx;
  }

  static boolean evalTerminalExpr(String expr) {
    return Boolean.parseBoolean(expr);
  }

  public static BDD apply(String op, BDD u1, BDD u2) {
    return new BDD(0);
  }

  public BDD restrict(BDD u, boolean varVal, int varNdx) {
    return new BDD(0);
  }

  public String toString() {
    return String.format("BDD@%d{size:%d}", System.identityHashCode(this), size);
  }

  public static void main(String [] args) {
  }
}