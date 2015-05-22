package logic;

import unit.TestCase;

public class ParserTest extends TestCase {

  Operator op = null;

  @Override
  public void tearDown() {
    op = null;
  }

  void evalOp(Operator op, boolean expectedTruthVal, Proposition ... props) {
    // System.out.printf("%s %s %s\n", op.getName(), expectedTruthVal, java.util.Arrays.toString(props));
    String strRep = props[0].toString();
    op.add(props[0]);
    for (int i = 1; i < props.length; i++) {
      Proposition p = props[i];
      op.add(p);
      strRep += String.format(" %s %s", op.getName(), p);
    }
    assertEquals(strRep, op.toString());
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
        evalOp(op, tt[i][j], p1, p2);
      }
    }
  }

  public void testAnd() {
    evalOpWithTable(And.class, new boolean[][]{{false, false}, {false, true}});
  }

  public void testAndMulti() {
    evalOp(new And(), true, Var.TRUE, Var.TRUE, Var.TRUE);

    evalOp(new And(), false, Var.TRUE, Var.FALSE, Var.FALSE);
    evalOp(new And(), false, Var.FALSE, Var.TRUE, Var.FALSE);
    evalOp(new And(), false, Var.FALSE, Var.FALSE, Var.TRUE);

    evalOp(new And(), false, Var.TRUE, Var.TRUE, Var.FALSE);
    evalOp(new And(), false, Var.FALSE, Var.TRUE, Var.TRUE);
    evalOp(new And(), false, Var.TRUE, Var.FALSE, Var.TRUE);

    evalOp(new And(), false, Var.FALSE, Var.FALSE, Var.FALSE);
  }

  public void testOr() {
    evalOpWithTable(Or.class, new boolean[][]{{false, true}, {true, true}});
  }

  public void testOrMulti() {
    evalOp(new Or(), true, Var.TRUE, Var.TRUE, Var.TRUE);

    evalOp(new Or(), true, Var.TRUE, Var.FALSE, Var.FALSE);
    evalOp(new Or(), true, Var.FALSE, Var.TRUE, Var.FALSE);
    evalOp(new Or(), true, Var.FALSE, Var.FALSE, Var.TRUE);

    evalOp(new Or(), true, Var.TRUE, Var.TRUE, Var.FALSE);
    evalOp(new Or(), true, Var.FALSE, Var.TRUE, Var.TRUE);
    evalOp(new Or(), true, Var.TRUE, Var.FALSE, Var.TRUE);

    evalOp(new Or(), false, Var.FALSE, Var.FALSE, Var.FALSE);
  }
}
