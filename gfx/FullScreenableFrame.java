package gfx;

import util.Flags;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;

/**
 * Utility class for creating a full-screen frame.
 *
 * @author Pablo Mayrgundter
 */
public class FullScreenableFrame extends JFrame {

  static Flags flags = new Flags(FullScreenableFrame.class);
  static final int WIDTH = flags.get("width", "w", 400);
  static final int HEIGHT = flags.get("height", "h", 400);
  static final boolean FULL_SCREEN = flags.get("fullScreen", "fs", false);

  protected int width, height;
  Graphics2D drawGraphics;

  public FullScreenableFrame() {
    this(WIDTH, HEIGHT);
  }

  public FullScreenableFrame(final int width, final int height) {
    this(width, height, FULL_SCREEN);
  }

  public FullScreenableFrame(final int width, final int height, final boolean fullscreen) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    if (fullscreen) {
      setUndecorated(true);
      final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      final GraphicsDevice device = env.getDefaultScreenDevice();
      final GraphicsConfiguration config = device.getDefaultConfiguration();
      device.setFullScreenWindow(this);
      this.width = (int)config.getBounds().getWidth();
      this.height = (int)config.getBounds().getHeight();
    } else {
      this.width = width;
      this.height = height;
      setSize(width, height);
    }
    setVisible(true);
    util.Sleep.sleep(100); // JRE Bug: graphics not ready immediately.
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Graphics2D getDrawGraphics() {
    return getDrawGraphics(null);
  }

  public Graphics2D getDrawGraphics(final Color bgColor) {
    return getDrawGraphics(bgColor, true);
  }

  @SuppressWarnings(value="unchecked")
  public Graphics2D getDrawGraphics(final Color bgColor, boolean antialias) {
    if (drawGraphics == null) {
      drawGraphics = (Graphics2D) getContentPane().getGraphics();
      if (antialias) {
        final java.util.Map hints = new java.util.HashMap();
        hints.put(java.awt.RenderingHints.KEY_ANTIALIASING,
                  java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        drawGraphics.setRenderingHints(hints);
      }
      if (bgColor != null) {
        drawGraphics.setColor(bgColor);
        drawGraphics.fillRect(0, 0, width, height);
      }
    }
    return drawGraphics;
  }

  public String toString() {
    return String.format("{%s@%d: width: %d, height: %d}",
                         this.getClass().getName(),
                         System.identityHashCode(this), width, height);
  }


  /**
   * Dummy value to quiet serialization checks.  This class should
   * not be serialized.
   */
  private static final long serialVersionUID = 1L;
}
