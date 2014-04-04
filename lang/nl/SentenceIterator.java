package lang.nl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Converts multi-line, multi-sentence input into an iterator of
 * single-line sentences, collapsing dash sentence endings.
 *
 * The sentence delimiter is "\\s*\\.\\s*".
 *
 * @author Pablo Mayrgundter
 */
public class SentenceIterator implements Iterator<String> {

  final Scanner sentenceScanner;

  public SentenceIterator(final Reader r) {
    sentenceScanner = new Scanner(r).useDelimiter("\\s*[.?!]\\s*");
  }

  public boolean hasNext() {
    return sentenceScanner.hasNext();
  }

  public String next() {
    return sentenceScanner.next().replaceAll("-\n", "").replaceAll("[\r\n]+", " ");
  }

  /** Unsupported operation. */
  public void remove() {
    throw new UnsupportedOperationException();
  }

  static final String TEST_INPUT = "a.\na b.\na b c.\na\nb c.\na b c. d e f.\na b. c\nd e f.";
  static final String EXPECTED_OUTPUT = "a\na b\na b c\na b c\na b c\nd e f\na b\nc d e f\n";
  @SuppressWarnings(value = "deprecation")
  static boolean test() {
    final SentenceIterator itr = new SentenceIterator(new StringReader(TEST_INPUT));
    String out = "";
    while (itr.hasNext())
      out += itr.next() + "\n";
    System.err.println("# TEST:\n"+ TEST_INPUT);
    System.err.println("# EXPECTED:\n"+ EXPECTED_OUTPUT);
    System.err.println("# ACTUAL:\n"+ out);
    return EXPECTED_OUTPUT.equals(out);
  }

  public static void main(final String [] args) throws Exception {
    if (args.length == 1 && args[0].equals("-test")) {
      if (!test()) {
        System.err.println("TESTS FAILED");
        System.exit(-1);
      }
      System.err.println("TESTS PASSED");
      return;
    }
    final SentenceIterator itr =
      new SentenceIterator(new InputStreamReader(System.in));
    while (itr.hasNext())
      System.out.println(itr.next());
  }
}
