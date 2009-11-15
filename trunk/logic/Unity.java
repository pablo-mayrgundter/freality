package logic;

final class Unity extends Expr {
  static final Unity UNITY = new Unity("1");
  private Unity(final String s) { super(s); }
}
