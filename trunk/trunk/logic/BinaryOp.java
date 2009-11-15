package logic;

class BinaryOp extends Op {

  final Expr mE1;
  final Expr mE2;

  BinaryOp(final String op, final Expr e1, final Expr e2) {
    super(op);
    mE1 = e1;
    mE2 = e2;
  }

  public String toString() {
    return mE1 +" "+ super.toString() +" "+ mE2;
  }
}
