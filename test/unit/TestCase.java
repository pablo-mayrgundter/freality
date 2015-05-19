package unit;

import util.Check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestCase extends Check<TestCase.TestException> {

  static class TestException extends RuntimeException {
    TestException(String msg) {
      super(msg);
    }
  }

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

  final List<String> errorMsgs;
  final List<Result> results;

  public TestCase() {
    errorMsgs = new ArrayList<String>();
    results = new ArrayList<Result>();
  }

  @Override
  protected void throwException(String msg) throws TestException {
    throw new TestException(msg);
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
            StackTraceElement ste = t.getStackTrace()[2];
            errorMsgs.add(String.format("%s\n\tat %s.%s(%s:%d)\n",
                t.getMessage(),
                ste.getClassName(),
                ste.getMethodName(),
                ste.getFileName(),
                ste.getLineNumber()));
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
