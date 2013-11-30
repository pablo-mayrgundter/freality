package unit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TestCase {

  enum Result {
    PASS("."), FAIL("F"), ERR("E");
    final String code;
    Result(String code) {
      this.code = code;
    }
    @Override
    public String toString() {
      return code;
    }
  }

  static class TestException extends RuntimeException {
    TestException(String msg) {
      super(msg);
    }
  }

  final List<String> errorMsgs;
  final List<Result> results;

  public TestCase() {
    errorMsgs = new ArrayList<String>();
    results = new ArrayList<Result>();
  }

  @Override
  public String toString() {
    String msg = "";
    for (String errorMsg : errorMsgs) {
      msg += errorMsg + "\n";
    }
    for (Result r : results) {
      msg += r;
    }
    return msg;
  }

  protected void setUp() {}
  protected void tearDown() {}

  protected void assertEquals(double a, double b) {
    assertEquals(new Double(a), new Double(b),
                 String.format("Expected %f, got %f", a, b));
  }

  protected void assertEquals(double[] a, double[] b, Object ... msg) {
    if (Arrays.equals(a, b)) {
      return;
    }
    throwMsgOrDiff(a, b, msg);
  }

  /** Prefer the msg if present over diff. */
  void throwMsgOrDiff(double[] a, double[] b, Object ... msg) {
    throw new TestException(msg.length > 0 ? format(msg) : diff(a, b));
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

  protected void assertEquals(Object a, Object b, Object ... msg) {
    if (a == null && b == null) {
      return;
    }
    if (!a.equals(b)) {
      throw new TestException(format(msg));
    }
  }

  protected void assertTrue(boolean b, Object ... msg) {
    if (!b) {
      throw new TestException(format(msg));
    }
  }

  protected void assertFalse(boolean b, Object ... msg) {
    if (b) {
      throw new TestException(format(msg));
    }
  }

  protected void fail(Object ... msg) {
    throw new TestException(format(msg));
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

  public TestCase run() {
    setUp();
    for (Method m : getTestMethods()) {
      try {
        m.invoke(this);
        results.add(Result.PASS);
      } catch (Exception e) {
        if (e instanceof InvocationTargetException) {
          Throwable t = ((InvocationTargetException) e).getTargetException();
          if (t instanceof TestException) {
            errorMsgs.add(m.getDeclaringClass().getName()
                          + "." + m.getName() + ": " + t.getMessage());
            results.add(Result.FAIL);
          } else {
            t.printStackTrace();
            results.add(Result.ERR);
          }
        } else {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }
    }
    tearDown();
    return this;
  }

  public void println() {
    System.out.println(this);
  }

  List<Method> getTestMethods() {
    Method[] methods = getClass().getDeclaredMethods();
    List<Method> testMethods = new ArrayList<Method>();
    for (Method m : methods) {
      if (m.getName().startsWith("test")
          && Modifier.isPublic(m.getModifiers())) {
        testMethods.add(m);
      }
    }
    return testMethods;
  }

  public static void main(String[] args) throws Exception {
    TestCase t = (TestCase) Class.forName(System.getProperty("sun.java.command")).newInstance();
    t.run().println();
  }
}
