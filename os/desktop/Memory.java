package os.desktop;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;

/**
 * The game of memory.
 *
 * To run standalone: java os.desktop.Memory
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class Memory extends Application implements ActionListener {

  static final long serialVersionUID = -1L;

  static { CLASS = Memory.class.getName(); } // HACK: Enables standalone.
  public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";

  class Button {
    final String text;
    boolean done;
    Button(String text) {
      this.text = text;
    }
  }

  final Font buttonFont = new Font("console", Font.BOLD, 14);

  Map<JButton, Button> buttonMap = new HashMap<JButton, Button>();

  Memory() {
    super(Utils.getRsrcProp("memory.name") + " " + CVS_VERSION.split(" ")[1]);
    setSize(200, 300);

    //    JTextField entry = new JTextField();
    //    entry.addActionListener(this);
    //    getContentPane().add(entry);

    final JPanel pad = new JPanel(new GridLayout(4,4));

    int buttonWidth = 4, buttonHeight = 4;
    Button [] buttons = new Button[buttonWidth * buttonHeight];
    for (int i = 0; i < buttonWidth; i++) {
      for (int j = 0; j < buttonHeight; j++) {
        buttons[buttonWidth * i + j] = new Button("" + i + j);
      }
    }

    for (Button b : buttons) {
      final JButton jb = new JButton();
      jb.setFont(buttonFont);
      jb.setText(b.text);
      jb.addActionListener(this);
      buttonMap.put(jb, b);
      pad.add(jb);
    }

    getContentPane().add(pad);
  }

  public void actionPerformed(ActionEvent e) {
    JButton jb = (JButton) e.getSource();
    Button b = buttonMap.get(jb);
    jb.setText(b.text);
    if (checkGuess(b)) {
      jb.setText("");
    }
  }

  Button cur = null, prev = null;

  boolean checkGuess(Button b) {
    prev = cur;
    cur = b;
    boolean clear = false;
    if (prev != null) {
      if (cur.text == prev.text) {
        cur.done = prev.done = true;
      } else {
        clear = true;
      }
      cur = prev = null;
    }
    return clear;
  }
}
