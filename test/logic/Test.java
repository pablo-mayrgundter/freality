package logic;

import unit.TestCase;

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
    assertTrue(op.isTrue(), "True AND True");
    assertEquals(True.VALUE, op.reduce());
  }

  public void testOr() {
    op = new Or();
    op.add(False.VALUE);
    op.add(True.VALUE);
    assertTrue(op.isTrue(), "False AND True");
    assertEquals(True.VALUE, op.reduce());
  }

  /**
   * Runnable as:
   *
   *   java logic.Test
   */
  public static void main(String [] args) {
    new Test().run().println();
  }
}