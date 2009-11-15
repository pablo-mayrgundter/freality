package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class CLI {
  static final BufferedReader mReader;
  static {
    mReader = new LineNumberReader(new InputStreamReader(System.in));
  }
  public static String readLine() {
    try {
      return mReader.readLine();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean promptYesNo() {
    return ((readLine()+" ").charAt(0)+"").equalsIgnoreCase("Y");
  }
}