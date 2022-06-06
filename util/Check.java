package util;

import java.util.Arrays;

public abstract class Check<T extends RuntimeException> {

  public static class RuntimeChecks extends Check<IllegalArgumentException> {
    @Override
    protected void throwException(String msg) throws IllegalArgumentException {
      throw new IllegalArgumentException(msg);
    }
  }

  static final Check check = new RuntimeChecks();

  protected abstract void throwException(String msg) throws T;

  // static wrappers.

  public static void check(boolean b, Object ... msg) {
    check.assertTrue(b, msg);
  }

  public static void checkFalse(boolean b, Object ... msg) {
    check.assertTrue(b, msg);
  }

  public static void checkEquals(double a, double b) {
    check.assertEquals(a, b);
  }

  public static void checkEquals(double a, double b, Object ... msg) {
    check.assertEquals(a, b, msg);
  }

  public static void checkEquals(Object a, Object b, Object ... msg) {
    check.assertEquals(a, b, msg);
  }

  // impls.

  public void assertTrue(boolean b, Object ... msg) {
    if (!b) {
      throwException(format(msg));
    }
  }

  public void assertFalse(boolean b, Object ... msg) {
    if (b) {
      throwException(format(msg));
    }
  }

  public void assertEquals(double a, double b) {
    assertEquals(Double.valueOf(a), Double.valueOf(b),
                 String.format("Expected %f, got %f", a, b));
  }

  public void assertEquals(double[] a, double[] b, Object ... msg) {
    if (Arrays.equals(a, b)) {
      return;
    }
    throwMsgOrDiff(a, b, msg);
  }

  /** Prefer the msg if present over diff. */
  void throwMsgOrDiff(double[] a, double[] b, Object ... msg) {
    throwException(msg.length > 0 ? format(msg) : diff(a, b));
  }

  String diff(double[] a, double[] b) {
    String msg = null;
    if (a.length != b.length) {
      msg = "Array lengths differ";
    } else {
      for (int i = 0; i < a.length; i++) {
        double aElt = a[i];
        double bElt = b[i];
        if (aElt != bElt) {
          msg = String.format("elts at index %d differ: %s != %s", i, aElt, bElt);
          break;
        }
      }
    }
    if (msg == null) throw new IllegalStateException("Couldn't find diff");
    return msg;
  }

  public void assertEquals(Object a, Object b, Object ... msg) {
    if (a == null && b == null) {
      return;
    }
    if (!a.equals(b)) {
      if (msg.length == 0) {
        throwException(String.format("Expected %s, got %s", a, b));
      } else {
        throwException(format(msg));
      }
    }
  }

  public void fail(Object ... msg) {
    throwException(format(msg));
  }

  String format(Object ... msg) {
    if (msg.length > 0) {
      if (!(msg[0] instanceof String)) {
        throw new IllegalArgumentException("First element of varargs must be a string");
      }
      String fmt = (String) msg[0];
      if (msg.length == 1) {
        return fmt;
      } else if (msg.length > 1) {
        Object [] args = new Object[msg.length - 1];
        System.arraycopy(msg, 1, args, 0, msg.length - 1);
        return String.format(fmt, args);
      }
    }
    return "";
  }
}
