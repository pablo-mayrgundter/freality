package bin;

public class Echo {
  static final String HELP =
      "Usage: /bin/echo [SHORT-OPTION]... [STRING]...\n" +
      "  or:  /bin/echo LONG-OPTION\n" +
      "Echo the STRING(s) to standard output.\n\n" +
      "  -n\t\tdo no output trailing newline\n" +
      "  -e\t\tenable interpretation of backslash escapes\n" +
      "  -E\t\tenable interpretation of backslash escapes (default)\n" +
      "      -help\tdisplay this help and exit\n" +
      "      -version\toutput version information and exit\n\n" +
      "If -e is in effect, the following sequences are recognized:\n\n" +
      "  \\0NNN   the character whose ASCII code is NNN (octal)\n" +
      "  \\     backslash\n" +
      "  \\a     alert (BEL)\n" +
      "  \\b     backspace\n" +
      "  \\c     produce no further output\n" +
      "  \\f     form feed\n" +
      "  \\n     new line\n" +
      "  \\r     carriage return\n" +
      "  \\t     horizontal tab\n" +
      "  \\v     vertical tab\n\n" +
      "NOTE: your shell may have its own version of echo, which usually supersedes\n" +
      "the version described here.  Please refer to your shell's documentation\n" +
      "for details about the options it supports.\n\n" +
      "Project home page and bug reports: <http://code.google.com/p/freality/>\n\0";

  static final String VERSION =
      "1.0 - written by Pablo Mayrgundter (derived from GNU coreutils)\n\0";

  @SuppressWarnings("fallthrough")
  public static void main(String [] args) {
    boolean flags = true, newline = true, escapes = false;
    int i = 0, words = 0;
    for (; i < args.length; i++) {
      if (flags && args[i].startsWith("-")) {
        char flag = args[i].charAt(1);
        switch (flag) {
          case 'n': newline = false; break;
          case 'e': escapes = true; break;
          case 'E': ; break;
          case 'v': System.out.println(VERSION); return;
          case 'h':; // fall through
          default: System.out.println(HELP); return;
        }
      } else {
        flags = false;
        if (words >= 1) {
          System.out.print(" ");
        }
        String arg = args[i];
        if (escapes) {
          char [] escaped = arg.toCharArray();
          int count = 0;
          for (int j = 0; j < escaped.length; j++) {
            char c = escaped[j];
            if (c == '\\') {
              c = escaped[j + 1];
              switch(c) {
                case 'a': c = (char)7; j++; break; // bell
                case 'b': c = '\b'; j++; break;
                case 'c': i = escaped.length; break; // no further output.
                case 'f': c = '\f'; j++; break;
                case 'n': c = '\n'; j++; break;
                case 'r': c = '\r'; j++; break;
                case 't': c = '\t'; j++; break;
                  //case 'v': c = '\v'; j++; break;
              }
            }
            escaped[count++] = c;
          }
          System.out.print(new String(escaped, 0, count));
        } else {
          System.out.print(args[i]);
        }
        words++;
      }
    }
    if (newline) {
      System.out.println();
    }
  }
}
