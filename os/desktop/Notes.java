package os.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.LinkedHashSet;
import javax.swing.*;

/**
 * Notes like MacOS Stickies.
 *
 * @version $Revision: 1.1.1.1 $
 */
class Notes implements ActionListener {

  //static { CLASS = Notes.class.getName(); } // HACK: Enables standalone.
  //public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";

  Set<JTextArea> notes = null;
  boolean firstTime = true;

  Notes() {
    //super(Utils.getRsrcProp("stickies.name") + " " + CVS_VERSION.split(" ")[1]);
  }

  void loadNotes() {
    notes = new LinkedHashSet<JTextArea>();
    // firstTime = false;
  }

  JTextArea newNote() {
    JTextArea textArea = new JTextArea(20, 80);
    notes.add(textArea);

    JFrame f = new JFrame();
    f.setSize(400, 400);
    f.add(textArea);
    f.setVisible(true);
    return textArea;
  }

  public void run() {
    loadNotes();
    if (firstTime) {
      JTextArea note = newNote();
      note.append("Welcome to Notes!");
    }

    final JMenuBar mb = new JMenuBar();
    final JMenu m = new JMenu(Utils.getRsrcProp("notes.name"));
    mb.add(m);
    addMenuItem(m, "Notes", (int) Utils.getRsrcProp("notes.shortcutKey").charAt(0));
  }

  void addMenuItem(JMenu m, String name, int shortcutKey) {
    final JMenuItem mi = new JMenuItem(name);
    //mi.setMnemonic(shortcutKey);
    mi.setActionCommand("Foo");
    mi.addActionListener(this);
    m.add(mi);
  }

  /** Handles menu events */
  public void actionPerformed(ActionEvent e) {
    newNote();
  }

  public static void main(String [] args) {
    new Notes().run();
  }
}
