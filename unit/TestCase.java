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
    PASS("👍"), FAIL("👎"), ERR("❌");
    final String code;
    Result(String code) {
      this.code = code;
    }
    @Override
    public String toString() {
      return code;
    }
  }

  private List<Method> testMethods;
  final List<Result> results;

  public TestCase() {
    results = new ArrayList<Result>();
  }

  @Override
  protected void throwException(String msg) throws TestException {
    throw new TestException(msg);
  }

  @Override
  public String toString() {
    int pass = 0, fail = 0, err = 0;
    for (Result r : results) {
      switch(r) {
      case PASS: pass++; break;
      case FAIL: fail++; break;
      case ERR: err++; break;
      }
    }
    return String.format("%s\n%d tests ran, %d pass, %d fail, %d err",
                         this.results, this.results.size(), pass, fail, err);
  }

  protected void setUp() {}
  protected void tearDown() {}

  public TestCase run() {
    setUp();
    testMethods = getTestMethods();
    for (Method m : testMethods) {
      try {
        m.invoke(this);
        results.add(Result.PASS);
      } catch (Exception e) {
        if (e instanceof InvocationTargetException) {
          Throwable t = ((InvocationTargetException) e).getTargetException();
          if (t instanceof TestException) {
            t.printStackTrace();
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

  List<Method> getTestMethods() {
    Method[] methods = getClass().getMethods();
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
    String clazz = System.getProperty("sun.java.command");
    TestCase t = (TestCase) Class.forName(clazz).newInstance();
    System.out.println("unit.TestCase: running tests");
    System.out.println(t.run());
  }
}
