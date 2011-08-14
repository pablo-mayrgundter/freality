package jos.desktop;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import jos.util.Debug;

/**
 * A simple terminal that invokes Bash.
 *
 * To run standalone: java jos.desktop.Terminal
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
@SuppressWarnings(value="serial")
class Terminal extends Application implements DocumentListener {

  static { CLASS = Terminal.class.getName(); } // HACK: Enables standalone.
  public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";

  final SharedTextArea mConsole;

  final Process shell;
  final BufferedWriter shellOut;
  int mCaretBarrier;

  Terminal() {
    super(Utils.getRsrcProp("term.name") + " " + CVS_VERSION.split(" ")[1]);
    setSize(400, 200);

    mConsole = new SharedTextArea();
    mConsole.setBackground(java.awt.Color.gray);
    mConsole.setForeground(java.awt.Color.white);
    mConsole.setCaretColor(java.awt.Color.white);
    mConsole.setLineWrap(true);
    mConsole.setNavigationFilter(new NavigationFilter() {
        public void moveDot(NavigationFilter.FilterBypass fb, int pos, Position.Bias bias) {
          if (pos >= mCaretBarrier)
            super.moveDot(fb, pos, bias);
        }

        public void setDot(NavigationFilter.FilterBypass fb, int pos, Position.Bias bias) {
          if (pos >= mCaretBarrier)
            super.setDot(fb, pos, bias);
        }
      });

    JScrollPane scrollPane = new JScrollPane(mConsole);
    getContentPane().add(scrollPane);

    mConsole.getDocument().addDocumentListener(this);

    try {
      shell = Runtime.getRuntime().exec("/bin/bash -i");
      new Thread(new StreamDisplay(shell.getInputStream(), mConsole)).start();
      new Thread(new StreamDisplay(shell.getErrorStream(), mConsole)).start();
      shellOut = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
    } catch (IOException e) {
      assert Debug.trace(e);
      throw new IllegalStateException(Utils.getRsrcProp("term.startErr"));
    }
  }

  public void insertUpdate(DocumentEvent e) {
    synchronized (mConsole) {
      if (!mConsole.mSkip) {
        final String command = mConsole.getLastAdd();
        if (command.endsWith("\n")) {
          try {
            shellOut.write(command, 0, command.length());
            shellOut.flush();
          } catch (IOException ioe) {
            assert Debug.trace(ioe);
          }
        }
      }
    }
  }

  public void changedUpdate(DocumentEvent e) { }
  public void removeUpdate(DocumentEvent e) { }

  @SuppressWarnings(value="serial")
  class SharedTextArea extends JTextArea {

    /**
     * A mutex flag.. if the display thread is writing to the text
     * area, the input thread shouldn't listen to insertUpdate
     * events.
     */
    boolean mSkip;
    int mLastCaret;

    SharedTextArea() {
      mSkip = false;
      mLastCaret = 0;
    }

    void addText(String text) {
      mSkip = true;
      // This bit here about grabbing the whole text
      // frequently should be optimized.
      final String newText = getText() + text;
      setText(newText);
      final int length = newText.length();
      // set AND move to keep a selection from being made.
      mLastCaret = length;
      mCaretBarrier = length;
      setCaretPosition(length);
      moveCaretPosition(length);
      mSkip = false;
    }

    String getLastAdd() {
      return getText().substring(mLastCaret);
    }
  }

  class StreamDisplay implements Runnable {

    final BufferedReader mReader;
    final SharedTextArea mTextComponent;

    StreamDisplay(InputStream is, SharedTextArea tc) {
      mReader = new BufferedReader(new InputStreamReader(is));
      mTextComponent = tc;
    }

    public void run() {
      try {
        final char [] cbuf = new char[4096];
        int length;
        while ((length = mReader.read(cbuf)) != -1) {
          synchronized (mTextComponent) {
            mTextComponent.addText(new String(cbuf, 0, length));
          }
        }
      } catch (Exception e) {
        assert Debug.trace(e);
      }
    }
  }
}
