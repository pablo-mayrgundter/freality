package al.gene;

import java.util.ArrayList;
import java.util.Random;

class Util {

  static final Random R = new Random();

  static String [] bigrams(final String s) {
    final ArrayList<String> bigramList = new ArrayList<String>();
    for (int i = 0; i < s.length() - 1; i++)
      bigramList.add(s.substring(i, i+2));
    return bigramList.toArray(new String[]{});
  }

  static char randChar() {
    return (char)(32 + R.nextInt(94));
  }

  static char randLower() {
    return (char)(97 + R.nextInt(26));
  }

  static void flipChar(final StringBuffer buf) {
    buf.setCharAt(R.nextInt(buf.length()), randLower());
  }

  static String randString(final int len) {
    final StringBuffer buf = new StringBuffer(len);
    buf.setLength(len);
    for (int i = 0; i < len; i++)
      buf.setCharAt(i, randChar());
    return buf.toString();
  }
}