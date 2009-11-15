package logic;

import java.io.PrintStream;
import static logic.Bool.TRUE;
import static logic.Bool.FALSE;
import static logic.Unity.UNITY;

public class Theorem {

  static Expr [] parse (final String [] exprSyms) {
    final Expr [] exprs = new Expr[exprSyms.length];
    for (int i = 0; i < exprSyms.length; i++)
      exprs[i] = parse(exprSyms[i]);
    return exprs;
  }

  static Expr parse (final String exprSyms) {
    if (exprSyms.equals(TRUE.toString()))
      return TRUE;
    String [] parts = exprSyms.split("\\s*=\\s*");
    if (parts.length == 2)
      return new Equals(parse(parts[0]), parse(parts[1]));
    parts = exprSyms.split("\\s*&\\s*");
    if (parts.length == 2)
      return new And(parse(parts[0]), parse(parts[1]));
    parts = exprSyms.split("\\s*\\|\\s*");
    if (parts.length == 2)
      return new Or(parse(parts[0]), parse(parts[1]));
    parts = exprSyms.split("\\s*\\^\\s*");
    if (parts.length == 2)
      return new Xor(parse(parts[0]), parse(parts[1]));
    return FALSE;
  }

  static Bool valid (final Expr e) {
    if (e instanceof BinaryOp)
      if (e instanceof Equals)
        return ((Equals) e).mE1 == ((Equals) e).mE2 ? TRUE : FALSE;
      else if (e instanceof And)
        return ((And) e).mE1 == TRUE ? (((And) e).mE2 == TRUE ? TRUE : FALSE) : FALSE;
      else if (e instanceof Or)
        return ((Or) e).mE1 == TRUE ? TRUE : (((Or) e).mE2 == TRUE ? TRUE : FALSE);
      else if (e instanceof Xor)
        return ((Xor) e).mE1 == TRUE ?
          (((Xor) e).mE2 == FALSE ? TRUE : FALSE) : (((Xor) e).mE2 == TRUE ? TRUE : FALSE);
    return e == TRUE ? TRUE : FALSE;
  }

  public static void main (String [] args) throws Exception {
    final Expr e = parse(args[0]);
    final PrintStream out = new PrintStream(System.out, true, "UTF-8");
    out.println("Parsed: " + e);
    out.println("Valid:  " + valid(e));
  }
}
