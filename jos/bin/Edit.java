package jos.bin;

import gfx.vt.VT100;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

class Edit implements Runnable {

  final InputStream mIn;
  final PrintStream mOut;
  final int mLines;
  final int mCols;

  State mState;
  StringBuffer mDoc;
  int mL;
  int mC;

  Edit(final int lines, final int colums) throws UnsupportedEncodingException {
    mLines = lines;
    mCols = colums;
    mIn = System.in;
    mOut = new PrintStream(System.out, true, "UTF-8");
    mState = State.INIT;
    mDoc = new StringBuffer();
  }

  void load(final String filename) throws IOException {
    BufferedReader r = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = r.readLine()) != null)
      mDoc.append(line).append('\n');
    mOut.print(mDoc.toString());
    System.out.print(VT100.CURSOR_HOME);
  }

  @SuppressWarnings(value="fallthrough")
  public void run() {
    while (true) {
      mState = State.EDIT;
      try {
        final char c = (char) mIn.read();
        mOut.println("c: " + c);
        switch (c) {
        case '\n':   mDoc.append(c); mL++; mC = 0;
        case '\030':
          mOut.print(statLine() + "Saving...");
          final FileWriter w = new FileWriter("buf.out");
          w.write(mDoc.toString());
          w.flush(); w.close();
          mOut.print(VT100.cursorDirect(mL, mC));
          break;
        case '\033':
          mState = State.ESC;
          mOut.print('\033');
        default:
          switch (mState) {
          case ESC:
            mOut.print(c);
            mState = State.ESC_LB;
            break;
          case ESC_LB:
            switch (c) {
            case 'A': mOut.print(VT100.CURSOR_UP); break;
            case 'B': mOut.print(VT100.CURSOR_DOWN); break;
            case 'C': mOut.print(VT100.CURSOR_FORWARD); break;
            case 'D': mOut.print(VT100.CURSOR_BACKWARD); break;
            }
            mState = State.EDIT;
          default:
            mOut.print(c);
            break;
          }
          mC++;
          mDoc.append(c);
          // mOut.print(c >= 32 && c <= 126 || c == 10 ? c : "Unhandled: " + c + "\n");
        }
      } catch (IOException e) {
        System.err.println(e);
      }
    }
  }

  String modeLine() {
    return VT100.cursorDirect(mLines - 1,1);
  }

  String statLine() {
    return VT100.cursorDirect(mLines,1);
  }

  String redraw() {
    String s = VT100.CURSOR_TO_APP
      + "\033[=" // ??
      + modeLine()
      + mState
      + " -- "
      + "(New File)";
    s += VT100.CURSOR_HOME;
    for (int i = 0; i < mLines - 1; i++)
      s += VT100.ERASE_TO_EOL;
    s += "\033[A\033[H\033["+mLines+";1H\033[K\033[?1l\033>\033[H";
    return s;
  }

  public static void main(final String [] args) throws Exception {
    String s;
    final int lines = Integer.parseInt(System.getProperty("LINES", "38"));
    final int cols = Integer.parseInt(System.getProperty("COLUMNS", "125"));
    final Edit e = new Edit(lines, cols);
    //        System.out.println("System.in.getClass(): " + System.in.getClass());
    //        if (true)
    //            return;
    System.out.print(e.redraw());
    if (args.length > 0)
      e.load(args[0]);
    new Thread(e).start();
  }

  static enum State {
    INIT, EDIT, ESC, ESC_LB;
  }
}
