package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

public class CLI {

  final BufferedReader in;
  final PrintStream out;
  final PrintStream err;

  public CLI() {
    this(System.in, System.out, System.err);
  }

  public CLI(InputStream in, PrintStream out, PrintStream err) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = out;
    this.err = err;
  }

  public String readLine() {
    try {
      return in.readLine();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public boolean promptYesNo(String msg) {
    out.println(msg);
    return Boolean.parseBoolean(readLine());
  }
}