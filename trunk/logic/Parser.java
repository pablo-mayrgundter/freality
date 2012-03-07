package logic;

import java.io.PrintStream;

public class Parser {

  static Proposition [] parse (final String [] args) {
    final Proposition [] props = new Proposition[args.length];
    for (int i = 0; i < args.length; i++)
      props[i] = parse(args[i]);
    return props;
  }

  static Proposition parse (final String prop) {
    if (prop.equalsIgnoreCase(True.VALUE.toString()))
      return True.VALUE;
    String [] parts = prop.split("\\s*=\\s*");
    if (parts.length == 2)
      return new Equals(parse(parts[0]), parse(parts[1]));
    parts = prop.split("\\s*&\\s*");
    if (parts.length == 2)
      return new And(parse(parts[0]), parse(parts[1]));
    parts = prop.split("\\s*\\|\\s*");
    if (parts.length == 2)
      return new Or(parse(parts[0]), parse(parts[1]));
    parts = prop.split("\\s*\\^\\s*");
    if (parts.length == 2)
      return new Xor(parse(parts[0]), parse(parts[1]));
    return False.VALUE;
  }

  public static void main (String [] args) throws Exception {
    final Proposition p = parse(args[0]);
    final PrintStream out = new PrintStream(System.out, true, "UTF-8");
    out.println("Parsed: " + p);
    out.println("Bound:  " + p.isBound());
    out.println("Valid:  " + p.isTrue());
  }
}
