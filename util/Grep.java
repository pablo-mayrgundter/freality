package util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Grep implementation using Java regex.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
public class Grep implements Runnable {

  final Matcher matcher;
  final BufferedReader bufferedReader;
  final PrintStream printStream;
  boolean match;

  Grep(final Matcher matcher, final Reader reader, final PrintStream printStream) {
    this.matcher = matcher;
    this.bufferedReader = new BufferedReader(reader);
    this.printStream = printStream;
  }

  public void run() {
    int lineCount = 0;
    String line;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        matcher.reset(line);
        if (matcher.find()) {
          printStream.printf("%s\n", line);
          match = true;
        }
        lineCount++;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String [] args) throws IOException {
    if (args.length == 0) {
      System.err.printf("Usage: java %s PATTERN [FILE]\n", Grep.class.getName());
      System.exit(1);
    }
    Grep g = new Grep(Pattern.compile(args[0]).matcher(""),
                      args.length == 2 ? new FileReader(args[1]) : new InputStreamReader(System.in),
                      System.out);
    g.run();
    System.exit(g.match ? 0 : 1);
  }
}
