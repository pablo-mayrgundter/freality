package lang.pl.slang;

import java.io.IOException;

class Shell {
  public static void main(String [] args)
    throws IOException,
           ClassNotFoundException,
           InstantiationException,
           IllegalAccessException {
    Eval.run("UserSession", args[0]);
  }
}
