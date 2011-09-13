package util;

/**
 * Helper class to assemble flags in bulk then check them once.
 * Usage:
 *
 *   // Required flags have no default specified.
 *   String first = Flags.get("first");
 *   String last = Flags.get("last");
 *   // Optional flags have are called with a default value, which can
 *   // be null.
 *   String middle = Flags.getOpt("middle", null);
 *   if (!Flags.check("Must specify first and last name.")) { return; }
 *
 * @author Pablo Mayrgundter
 * @version Sat Aug 13 18:46:32 EDT 2011
 */
public class Flags {

  static boolean flagsOk = true;

  public static void clear() {
    flagsOk =true;
  }

  public static boolean check(String msg) {
    if (flagsOk) {
      return true;
    }
    System.err.println(msg);
    return false;
  }

  // String flags.

  public static String get(String name) {
    String val = System.getProperty(name);
    if (val == null) {
      flagsOk = false;
    }
    return null;
  }

  public static String get(String name, String defaultVal) {
    return System.getProperty(name, defaultVal);
  }

  // Int flags.

  public static int getInt(String name) {
    return Integer.parseInt(get(name, "0"));
  }

  public static int getInt(String name, int defaultVal) {
    String strVal = System.getProperty(name);
    if (strVal != null) {
      return Integer.parseInt(strVal);
    }
    return defaultVal;
  }

  // Boolean flags.

  public static boolean getBool(String name) {
    return Boolean.parseBoolean(get(name, "false"));
  }

  public static boolean getBool(String name, boolean defaultVal) {
    String strVal = System.getProperty(name);
    if (strVal != null) {
      return Boolean.parseBoolean(strVal);
    }
    return defaultVal;
  }
}