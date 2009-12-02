package jos.desktop;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;

/**
 * The Application class provides shared functionality the simple
 * desktokp applications.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.2 $
 */
public abstract class Application extends JInternalFrame {

  protected Application() {}

  protected Application(String name) {
    super(name,
          true, // resizable
          true, // closable
          true, // maximizable
          true); // iconifiable
    setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
  }

  public static String CLASS;
  public static void main(String [] args) {

    Application app = null;
    try {
      app = (Application) Class.forName(CLASS).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    Component [] components = app.getComponents();
    LayoutManager layout = app.getLayout();
    final JFrame frame = new JFrame();
    frame.getContentPane().setLayout(layout);

    for (Component c : components)
      frame.getContentPane().add(c);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    Insets appInsets = app.getInsets();
    int width = app.getWidth() + appInsets.left + appInsets.right;
    int height = app.getHeight() + appInsets.top + appInsets.bottom;
    height += frame.getInsets().top;
    frame.setSize(width, height);
  }
}
