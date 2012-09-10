package util;

public class Diff {
  public static String diff(Object [] a, Object [] b) {
    String msg = "";
    for (int i = 0, n = Math.min(a.length, b.length); i < n; i++) {
      if (!a[i].equals(b[i])) {
        if (msg.length() > 0) {
          msg += "\n";
        }
        msg += String.format("a[%d](%s) != b[%d](%s)", i, a[i], i, b[i]);
      }
    }
    return msg;
  }
  public static String diff(int [] a, int [] b) {
    String msg = "";
    for (int i = 0, n = Math.min(a.length, b.length); i < n; i++) {
      if (a[i] != b[i]) {
        if (msg.length() > 0) {
          msg += "\n";
        }
        msg += String.format("a[%d](%s) != b[%d](%s)", i, a[i], i, b[i]);
      }
    }
    if (a.length != b.length) {
      if (msg.length() > 0) {
        msg += "\n";
      }
      msg += String.format("a.length() != b.length", a.length, b.length);
    }
    return msg;
  }
  public static String diff(int a, int b) {
    if (a == b) {
      return "";
    }
    return String.format("%d != %d", a, b);
  }
}