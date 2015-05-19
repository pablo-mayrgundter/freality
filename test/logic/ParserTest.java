package logic;

import unit.TestCase;

public class ParserTest extends TestCase {

  Operator op = null;

  @Override
  public void tearDown() {
    op = null;
  }

  // TODO(pablo): use Boolean instead? Also get autoboxing then.

  void evalOp(Operator op, Proposition p1, Proposition p2, boolean expectedTruthVal) {
    op.add(p1);
    op.add(p2);
    assertEquals(String.format("%s %s %s", p1, op.getName(), p2), op.toString());
    // System.out.printf("%s %s %s %s %s\n", op, p1, p2, expectedTruthVal, op.isTrue());
    assertEquals(expectedTruthVal, op.isTrue());
    assertEquals(expectedTruthVal ? Var.TRUE : Var.FALSE, op.reduce());
  }

  void evalOpWithTable(Class<? extends Operator> opClazz, boolean [][] tt) {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        Proposition p1 = i == 0 ? Var.FALSE : Var.TRUE;
        Proposition p2 = j == 0 ? Var.FALSE : Var.TRUE;
        Operator op = null;
        try {
          op = opClazz.newInstance();
        } catch (Exception e) {
          throw new IllegalStateException();
        }
        evalOp(op, p1, p2, tt[i][j]);
      }
    }
  }

  public void testAnd() {
    evalOpWithTable(And.class, new boolean[][]{{false, false}, {false, true}});
  }

  public void testOr() {
    evalOpWithTable(Or.class, new boolean[][]{{false, true}, {true, true}});
  }
}
