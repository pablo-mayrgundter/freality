package jos.desktop;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple calculator.
 *
 * To run standalone: java jos.desktop.Calculator
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class Calculator extends Application implements ActionListener {

  static final long serialVersionUID = -6893984478913168872L;

  static { CLASS = Calculator.class.getName(); } // HACK: Enables standalone.
  public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";

  enum Button {

    ONE (Utils.getRsrcProp("calc.one")),
    TWO (Utils.getRsrcProp("calc.two")),
    THREE (Utils.getRsrcProp("calc.three")),
    FOUR (Utils.getRsrcProp("calc.four")),
    FIVE (Utils.getRsrcProp("calc.five")),
    SIX (Utils.getRsrcProp("calc.six")),
    SEVEN (Utils.getRsrcProp("calc.seven")),
    EIGHT (Utils.getRsrcProp("calc.eight")),
    NINE (Utils.getRsrcProp("calc.nine")),
    ZERO (Utils.getRsrcProp("calc.zero")),

    EQUALS (Utils.getRsrcProp("calc.equals")),
    CLEAR (Utils.getRsrcProp("calc.clear")),
    ADD (Utils.getRsrcProp("calc.add")),
    SUB (Utils.getRsrcProp("calc.sub")),
    MUL (Utils.getRsrcProp("calc.mul")),
    DIV (Utils.getRsrcProp("calc.div"));

    private final String displayName;
    Button(String displayName) {
      this.displayName = displayName;
    }

    String getDisplayName() { return displayName; }
  }

  final Font buttonFont = new Font("console", Font.BOLD, 14);
  final JTextField mDisplay;
  final Map<String, Button> buttonLookup;

  float val;
  String accumulator;
  Button op = null;

  Calculator() {
    super(Utils.getRsrcProp("calc.name") + " " + CVS_VERSION.split(" ")[1]);
    setSize(200, 300);

    //    JTextField entry = new JTextField();
    //    entry.addActionListener(this);
    //    getContentPane().add(entry);

    mDisplay = new JTextField();
    mDisplay.setHorizontalAlignment(JTextField.RIGHT);

    final JPanel pad = new JPanel(new GridLayout(4,2));

    final Button [] buttons = new Button[]{ Button.ONE, Button.TWO, Button.THREE, Button.ADD,
                                            Button.FOUR, Button.FIVE, Button.SIX, Button.SUB,
                                            Button.SEVEN, Button.EIGHT, Button.NINE, Button.MUL,
                                            Button.CLEAR, Button.ZERO, Button.EQUALS, Button.DIV };

    buttonLookup = new HashMap<String, Button>();

    for (Button b : buttons) {
      final JButton button = new JButton(b.getDisplayName());
      button.setFont(buttonFont);
      pad.add(button);
      button.addActionListener(this);
      buttonLookup.put(b.getDisplayName(), b);
    }

    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                                                new JScrollPane(mDisplay),
                                                pad);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(30);
    getContentPane().add(splitPane);
    val = 0;
    accumulator = "";
  }

  @SuppressWarnings(value="fallthrough")
  public void actionPerformed(ActionEvent e) {
    final Button button = buttonLookup.get(e.getActionCommand());

    switch (button) {

    case ADD:;
    case SUB:;
    case MUL:;
    case DIV: {
      op = button;
      if (!accumulator.equals("")) {
        val = Float.parseFloat(accumulator);
        accumulator = "";
      }
    } break;

    case EQUALS: {
      if (accumulator.equals("") || op == null) return;
      compute(op, Float.parseFloat(accumulator)); accumulator = ""; 
    } break;

    case CLEAR: {
      val = 0;
      accumulator = "";
      mDisplay.setText("");
    } break;

      // Numbers.
    default: accumulator += button.getDisplayName(); mDisplay.setText(accumulator);
    }
  }

  void compute(Button op, float newVal) {
    switch (op) {
    case ADD: val += newVal; break;
    case SUB: val -= newVal; break;
    case MUL: val *= newVal; break;
    case DIV: val /= newVal; break;
    }
    mDisplay.setText(Float.toString(val));
  }
}
