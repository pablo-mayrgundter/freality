package unit;

import static unit.TestCase.Result;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TestSuite extends LinkedHashSet<TestCase> {

  final List<Result> results;

  public TestSuite() {
    results = new ArrayList<Result>();
  }

  public void addTest(TestCase t) {
    add(t);
  }

  public void addTest(TestSuite ts) {
    addTestSuite(ts);
  }

  public void addTest(Class<? extends TestCase> tsClass) {
    try {
      addTest(tsClass.newInstance());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void addTestSuite(TestSuite ts) {
    addAll(ts);
  }

  public TestSuite run() {
    for (TestCase t : this) {
      t.run();
      results.addAll(t.results);
    }
    return this;
  }

  @Override
  public String toString() {
    String msg = "";
    for (Result r : results) {
      msg += r;
    }
    return String.format("%d TestCases with %d tests run:\n%s",
                         size(), results.size(), msg);
  }

  public void println() {
    System.out.println(this);
  }
}
