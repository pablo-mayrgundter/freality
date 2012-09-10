package os.util;

public class Debug {

  public static final int INFO = 0;
  public static final int WARNING = 1;
  public static final int ERROR = 2;

  static boolean DEBUG = Boolean.getBoolean("debug");
  static int LEVEL = Integer.parseInt(System.getProperty("debugLevel", Integer.toString(WARNING)));

  public static boolean isOn() { return DEBUG; }

  public static void set(boolean on) { DEBUG = on; }
  public static void setLevel(int level) { LEVEL = level; }

  public static boolean trace(Exception e) {
    e.printStackTrace();
    return true;
  }

  /** Equivalent to println(str, INFO) */
  public static boolean println(String line) {
    return println(line, INFO);
  }

  public static boolean println(String line, int level) {
    if (level > LEVEL) {
      System.err.println(line);
    }
    return true;
  }
}
