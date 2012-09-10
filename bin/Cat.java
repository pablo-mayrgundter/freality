package bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

/**
 * Catenate standard input or a file to standard output.
 *
 * @author Pablo Mayrgundter
 */
class Cat implements Runnable {

  final BufferedReader in;
  final PrintStream out;
  boolean lineProcessor = true;
  int lineCount;

  Cat(final Reader reader, final PrintStream printStream) {
    in = new BufferedReader(reader);
    out = printStream;
  }

  public void run() {
    try {
      String line;
      while ((line = in.readLine()) != null) {
        processLine(line);
        lineCount++;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected void processLine(String line) {
    out.println(line);
  }

  static boolean checkArgs(String [] args) {
    if (args.length == 1 && !new File(args[0]).exists()) {
      System.err.printf("Usage: java %s [FILE]\n", Cat.class.getName());
      return false;
    }
    return true;
  }

  static String getHelp() {
    return String.format("Usage: java %s [FILE]\n", Cat.class.getName());
  }

  /**
   * @return A reader for System.in iff args.length - 1 < ndx, else
   * new FileReader(args[ndx]);
   */
  static Reader getReader(String [] args, int ndx) throws IOException {
    if (args.length - 1 < ndx) {
      return new InputStreamReader(System.in);
    } else {
      return new FileReader(args[ndx]);
    }
  }

  public static void main(String [] args) throws IOException {
    if (!checkArgs(args)) {
      System.err.println(getHelp());
      System.exit(1);
    }
    new Cat(getReader(args, 0), System.out).run();
  }
}
