package algs;

public final class Util {
  public static boolean p(final String format, Object ... args) {
    System.err.printf(format+"\n", args);
    return true;
  }
  public static void swap(final int [] vals, int x, int y) {
    final int t = vals[x];
    vals[x] = vals[y];
    vals[y] = t;
  }
}