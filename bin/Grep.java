package bin;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Grep implementation using Java regex.
 *
 * @author Pablo Maygundter
 */
public class Grep extends Cat {

  final Matcher matcher;
  boolean match;

  Grep(String regex, Reader r, PrintStream p) {
    super(r, p);
    this.matcher = Pattern.compile(regex).matcher("");
  }

  public void processLine(String line) {
    matcher.reset(line);
    if (matcher.find()) {
      out.printf("%s\n", line);
      match = true;
    }
  }

  // TODO(pablo): shouldn't need to override these from Cat.
  static boolean checkArgs(String [] args) {
    if (args.length == 1 && !new File(args[0]).exists()) {
      System.err.printf("Usage: java %s [FILE]\n", Cat.class.getName());
      return false;
    }
    return true;
  }

  static String getHelp() {
    return String.format("Usage: java %s PATTERN [FILE]\n", Grep.class.getName());
  }

  public static void main(String [] args) throws IOException {
    if (!checkArgs(args)) {
      System.err.println(getHelp());
      System.exit(1);
    }
    Grep g = new Grep(args[0], getReader(args, 1), System.out);
    g.run();
    System.exit(g.match ? 0 : 1);
  }
}
