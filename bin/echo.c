/**
 * UNIX-style echo command.
 *
 * @author Pablo Mayrgundter
 * @date Sat Apr  7 17:54:45 EDT 2012
 */
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <string.h>

int main (const int argc, const char * const argv[]) {

  char * HELP =
      "Usage: /bin/echo [SHORT-OPTION]... [STRING]...\n"
      "  or:  /bin/echo LONG-OPTION\n"
      "Echo the STRING(s) to standard output.\n\n"
      "  -n\t\tdo no output trailing newline\n"
      "  -e\t\tenable interpretation of backslash escapes\n"
      "  -E\t\tenable interpretation of backslash escapes (default)\n"
      "      -help\tdisplay this help and exit\n"
      "      -version\toutput version information and exit\n\n"
      "If -e is in effect, the following sequences are recognized:\n\n"
      "  \\0NNN   the character whose ASCII code is NNN (octal)\n"
      "  \\     backslash\n"
      "  \\a     alert (BEL)\n"
      "  \\b     backspace\n"
      "  \\c     produce no further output\n"
      "  \\f     form feed\n"
      "  \\n     new line\n"
      "  \\r     carriage return\n"
      "  \\t     horizontal tab\n"
      "  \\v     vertical tab\n\n"
      "NOTE: your shell may have its own version of echo, which usually supersedes\n"
      "the version described here.  Please refer to your shell's documentation\n"
      "for details about the options it supports.\n\n"
      "Project home page and bug reports: <http://code.google.com/p/freality/>\n\0";

  char * VERSION =
    "1.0 - written by Pablo Mayrgundter (derived from GNU coreutils)\n\0";

  int flags = 1, newline = 1, escapes = 0;
  int i = 1, words = 0;
  for (; i < argc; i++) {
    if (flags && index(argv[i], '-')) {
      char flag = argv[i][1];
      switch (flag) {
      case 'n': newline = 0; break;
      case 'e': escapes = 1; break;
      case 'E': ; break;
      case 'v': printf("%s\n", VERSION); return;
      case 'h':; // fall through
      default: printf("%s\n", HELP); return;
      }
    } else {
      flags = 0;
      if (words >= 1) {
        printf(" ");
      }
      const char * arg = argv[i];
      if (escapes) {
        int argLen = strlen(arg);
        char * escaped = (char *) malloc(sizeof(char) * argLen);
        strcpy(escaped, arg);
        int count = 0, j = 0;
        for (j = 0; j < argLen; j++) {
          char c = escaped[j];
          if (c == '\\') {
            c = escaped[j + 1];
            switch(c) {
            case 'a': c = (char)7; j++; break; // bell
            case 'b': c = '\b'; j++; break;
            case 'c': i = argLen; break; // no further output.
            case 'f': c = '\f'; j++; break;
            case 'n': c = '\n'; j++; break;
            case 'r': c = '\r'; j++; break;
            case 't': c = '\t'; j++; break;
              //case 'v': c = '\v'; j++; break;
            }
          }
          escaped[count++] = c;
        }
        printf("%s", escaped);
      } else {
        printf("%s", argv[i]);
      }
      words++;
    }
  }
  if (newline) {
    printf("\n");
  }
  return 0;
}
