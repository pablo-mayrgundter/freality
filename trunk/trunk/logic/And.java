package logic;

class And extends BinaryOp {
  And (final Expr e1, final Expr e2) {
    super("&", e1, e2);
  }
}
