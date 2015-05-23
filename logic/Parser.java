package logic;

import java.io.PrintStream;

public class Parser {

  static Proposition [] parse (final String [] args) {
    final Proposition [] props = new Proposition[args.length];
    for (int i = 0; i < args.length; i++) {
      props[i] = parse(args[i]);
    }
    return props;
  }

  static Proposition parse(final String prop) {
    if (prop.equalsIgnoreCase(Var.TRUE.getName())) {
      return Var.TRUE;
    } else if (prop.equalsIgnoreCase(Var.FALSE.getName())) {
      return Var.FALSE;
    }
    String [] parts;
    parts = prop.split("\\s+(\\||OR)\\s+");
    if (parts.length >= 2) {
      // System.out.println("or split: " + java.util.Arrays.asList(parts));
      Or expr = new Or();
      for (String p : parts) {
        expr.add(parse(p));
      }
      return expr;
    }
    parts = prop.split("\\s+(&|AND)\\s+");
    if (parts.length >= 2) {
      // System.out.println("and split: " + java.util.Arrays.asList(parts));
      And expr = new And();
      for (String p : parts) {
        expr.add(parse(p));
      }
      return expr;
    }
    parts = prop.split("\\s+(^|XOR)\\s+");
    if (parts.length == 2) {
      return new Xor(parse(parts[0]), parse(parts[1]));
    }
    parts = prop.split("\\s+=\\s+");
    if (parts.length == 2) {
      return new Equals(parse(parts[0]), parse(parts[1]));
    }
    throw new IllegalArgumentException("Expression not recognized: " + prop);
  }

  public static void main (String [] args) throws Exception {
    final Proposition p = parse(args[0]);
    final PrintStream out = new PrintStream(System.out, true, "UTF-8");
    out.println("Parsed as: " + p);
    out.println("Bound:     " + p.isBound());
    out.println("Is true:   " + p.isTrue());
  }
}
