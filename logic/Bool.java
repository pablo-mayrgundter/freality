package logic;

final class Bool extends Expr {
  public static final Bool TRUE = new Bool("True");
  public static final Bool FALSE = new Bool("False");
  private Bool(final String s) { super(s); }
}
