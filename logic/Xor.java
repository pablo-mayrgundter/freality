package logic;

class Xor extends BinaryOp {
  Xor(final Expr e1, final Expr e2) {
    super("^", e1, e2);
  }
}
