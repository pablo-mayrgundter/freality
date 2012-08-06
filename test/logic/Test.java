package logic;

import junit.framework.TestCase;

public class Test extends TestCase {

  Operator op = null;

  public void setUp() {
  }

  public void tearDown() {
    op = null;
  }

  // TODO(pablo): use Boolean instead? Also get autoboxing then.

  public void testAnd() {
    op = new And();
    op.add(True.VALUE);
    op.add(True.VALUE);
    assertTrue("True AND True", op.isTrue());
    assertEquals(True.VALUE, op.reduce());
  }

  public void testOr() {
    op = new Or();
    op.add(False.VALUE);
    op.add(True.VALUE);
    assertTrue("False AND True", op.isTrue());
    assertEquals(True.VALUE, op.reduce());
  }

  /**
   * Runnable as:
   *
   *   java logic.Test
   */
  public static void main(String [] args) {
    junit.textui.TestRunner.run(Test.class);
  }
}