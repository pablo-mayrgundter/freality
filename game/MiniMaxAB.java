package game;

import java.util.*;

class MiniMaxAB {

  static boolean MAX_PLAYER = true;

  MiniMaxAB(String [] heuristicVals) {
    List<IntNode> vals = new ArrayList<IntNode>();
    for (int i = 0; i < heuristicVals.length; i++) {
      vals.add(new IntNode(Integer.parseInt(heuristicVals[i])));
    }
    IntNode root = new IntNode();
    for (int i = 0; i < 3; i++) {
      IntNode n = new IntNode();
      n.children.add(vals.get(i / 3 + 0));
      n.children.add(vals.get(i / 3 + 1));
      n.children.add(vals.get(i / 3 + 2));
      root.children.add(n);
    }
    alphabeta(root, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    System.out.println(root);
  }

  int alphabeta(IntNode node, int depth, int alpha, int beta, boolean player) {
    if (depth == 0) {
      return node.val;
    }
    if (player == MAX_PLAYER) {
      for (IntNode child : node.children) {
        node.val = alpha = Math.max(alpha, alphabeta(child, depth - 1, alpha, beta, !player));
        if (beta <= alpha) {
          break;  // Beta cut-off
        }
      }
      return alpha;
    } else {
      for (IntNode child : node.children) {
        node.val = beta = Math.min(beta, alphabeta(child, depth - 1, alpha, beta, !player));
        if (beta <= alpha) {
          break; // Alpha cut-off
        }
        return beta;
      }
    }
    throw new IllegalStateException();
  }

  class IntNode {
    int val;
    List<IntNode> children = new ArrayList<IntNode>();
    IntNode() {
      this(-34);
    }
    IntNode(int x) {
      val = x;
    }
    public String toString() {
      return String.format("[%d]:\n%s", val, children);
    }
  }

  public static void main(String [] args) {
    new MiniMaxAB(args[0].split(" "));
  }
}
