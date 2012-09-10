package os.desktop;

import gfx.FullScreenableFrame;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.FontUIResource;
import os.desktop.people.*;
import os.util.Debug;

/**
 * A simple desktop environment.
 *
 * To run: java os.desktop.Desktop
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
@SuppressWarnings(value="serial")
public class Desktop extends FullScreenableFrame implements ActionListener {

  final String BROWSER = Utils.getRsrcProp("browser.name");
  final String CALC = Utils.getRsrcProp("calc.name");
  final String GROUPS = Utils.getRsrcProp("people.name");
  final String MAIL = Utils.getRsrcProp("mail.name");
  final String MEMORY = Utils.getRsrcProp("memory.name");
  final String NOTES = Utils.getRsrcProp("notes.name");
  final String PLAYER = Utils.getRsrcProp("player.name");
  final String QUIT = Utils.getRsrcProp("desktop.quit.name");
  final String SCREENSHOT = Utils.getRsrcProp("desktop.screenshot.name");
  final String TERMINAL = Utils.getRsrcProp("term.name");

  final JDesktopPane mDesktop;

  Desktop() {

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JFrame.setDefaultLookAndFeelDecorated(true);
    setJMenuBar(createMenubar());
    setVisible(true);
    mDesktop = new JDesktopPane();

    ImageIcon image = new ImageIcon("desktop.jpg");
    //        mDesktop.add(image, new Integer(0), 0);
    JLabel l = new JLabel(image);
    l.setBounds(0, 0, getWidth(), getHeight());
    ((JLayeredPane)mDesktop).add(l, JLayeredPane.FRAME_CONTENT_LAYER);

    //    mDesktop.setBackground(Color.WHITE);
    setContentPane(mDesktop);
  }

  class DefaultTheme extends DefaultMetalTheme {
    final FontUIResource DEFAULT_FONT =
      new FontUIResource(new Font("lucida", Font.PLAIN, 15));
    public FontUIResource getMenuTextFont() { return DEFAULT_FONT; }
    public FontUIResource getWindowTitleFont() { return DEFAULT_FONT; }
    public FontUIResource getSystemTextFont() { return DEFAULT_FONT; }
    public FontUIResource getUserTextFont() { return DEFAULT_FONT; }
    public FontUIResource getSubTextFont() { return DEFAULT_FONT; }
    public FontUIResource getControlTextFont() { return DEFAULT_FONT; }
  }

  //  public void dispose() {
    //    System.exit(0);
  //  }

  JMenuBar createMenubar() {

    final JMenuBar mb = new JMenuBar();

    final JMenu m = new JMenu(Utils.getRsrcProp("desktop.name"));
    m.setMnemonic((int) Utils.getRsrcProp("desktop.shortcutKey").charAt(0));
    mb.add(m);

    addMenuItem(m, BROWSER, (int) Utils.getRsrcProp("browser.shortcutKey").charAt(0));
    addMenuItem(m, CALC, (int) Utils.getRsrcProp("calc.shortcutKey").charAt(0));
    addMenuItem(m, MEMORY, (int) Utils.getRsrcProp("memory.shortcutKey").charAt(0));
    addMenuItem(m, NOTES, (int) Utils.getRsrcProp("notes.shortcutKey").charAt(0));
    addMenuItem(m, QUIT, (int) Utils.getRsrcProp("desktop.quit.shortcutKey").charAt(0));
    addMenuItem(m, SCREENSHOT, (int) Utils.getRsrcProp("desktop.screenshot.shortcutKey").charAt(0));
    addMenuItem(m, TERMINAL, (int) Utils.getRsrcProp("term.shortcutKey").charAt(0));

    //    addMenuItem(m, MAIL, (int) Utils.getRsrcProp("mail.shortcutKey").charAt(0));
    //    addMenuItem(m, GROUPS, (int) Utils.getRsrcProp("people.shortcutKey").charAt(0));
    //    try {
    //      Class.forName("os.desktop.Chat"); // no-op to load the class and so connect to chat network.
    //    } catch (Exception e) {
    //      assert Debug.trace(e);
    //    }
    //    addMenuItem(m, PLAYER, (int) Utils.getRsrcProp("player.shortcutKey").charAt(0));

    final Box b = new Box(BoxLayout.X_AXIS);
    final Clock clock = new Clock();
    mb.add(b);
    b.add(Box.createHorizontalGlue());
    b.add(clock);
    new Thread(clock).start();

    return mb;
  }

  void addMenuItem(JMenu m, String name, int shortcutKey) {
    final JMenuItem mi = new JMenuItem(name);
    mi.setMnemonic(shortcutKey);
    mi.setActionCommand(name);
    mi.addActionListener(this);
    m.add(mi);
  }

  /** Handles menu events */
  public void actionPerformed(ActionEvent e) {
    launchApp(e.getActionCommand());
  }

  /**
   * Can be called from actionPerformed as well as from other
   * applications.
   */
  Application launchApp(String appName) {
    appName = "os.desktop." + appName;
    try {
      return addApp((Application) Class.forName(appName).newInstance());
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Could not start application: "
                                         + appName + " (" + e + ")");
    }
  }

  public Application addApp(Application app) {
    app.setVisible(true);
    mDesktop.add(app);
    try {
      app.setSelected(true);
    } catch(java.beans.PropertyVetoException e) {
      // Ignore
    }
    return app;
  }

  void screenshot() {
    java.awt.image.BufferedImage bi =
      new java.awt.image.BufferedImage(getWidth(), getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bi.createGraphics();
    getRootPane().paint(g2d);
    g2d.dispose();
    java.io.File file = new java.io.File("ss.png");
    try {
      javax.imageio.ImageIO.write(bi, "png", file);
    } catch(java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  static Desktop DESKTOP_SINGLETON;
  public static Desktop getDesktop() {
    if (DESKTOP_SINGLETON == null)
      DESKTOP_SINGLETON = new Desktop();
    return DESKTOP_SINGLETON;
  }

  public static void main(String [] args) {
    getDesktop();
  }
}
