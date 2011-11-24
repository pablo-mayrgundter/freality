package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * A shell utility.
 *
 *  % java util.Shell
 *  hello  # input
 *  hello  # echo'd back
 */
public class Shell {

  final BufferedReader mReader;
  public Shell() {
    mReader = new LineNumberReader(new InputStreamReader(System.in));
  }

  public String readLine() {
    try {
      return mReader.readLine();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String [] args) {
    Shell shell = new Shell();
    String line = null;
    while ((line = shell.readLine()) != null) {
      System.out.println(line);
    }
  }
}