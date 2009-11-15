package ai;

public final class Util {
  public static boolean sleep (final int ms) {
    try { Thread.sleep(ms); } catch (InterruptedException e) { return false; }
    return true;
  }
  public static void p (final String format, final Object ... args) {
    System.out.printf(format, args);
  }
}