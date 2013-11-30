package unit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

  List<Result> results;

  public TestCase() {
    results = new ArrayList<Result>();
  }

  @Override
  public String toString() {
    return "" + results;
  }

  protected void setUp() {}
  protected void tearDown() {}

  protected void assertEquals(double a, double b) {
    assertEquals(new Double(a), new Double(b),
                 String.format("Expected %f, got %f", a, b));
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
      } catch (TestException e) {
        results.add(Result.FAIL);
      } catch (Exception e) {
        if (e instanceof InvocationTargetException) {
          ((InvocationTargetException) e).getTargetException().printStackTrace();
          results.add(Result.ERR);
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
      if (m.getName().startsWith("test")) {
        testMethods.add(m);
      }
    }
    return testMethods;
  }
}
