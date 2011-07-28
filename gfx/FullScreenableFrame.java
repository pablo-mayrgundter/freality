package gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;

public class FullScreenableFrame extends JFrame {

  static final int WIDTH = Integer.parseInt(System.getProperty("width", "400"));
  static final int HEIGHT = Integer.parseInt(System.getProperty("height", "400"));
  static final Boolean FULLSCREEN = Boolean.getBoolean("fs");

  int width, height;
  Graphics2D drawGraphics;

  public FullScreenableFrame() {
    this(WIDTH, HEIGHT, FULLSCREEN);
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
    return getDrawGraphics(Color.BLACK);
  }

  @SuppressWarnings(value="unchecked")
  public Graphics2D getDrawGraphics(final Color bgColor) {
    if (drawGraphics == null) {
      drawGraphics = (Graphics2D) getContentPane().getGraphics();
      final java.util.Map hints = new java.util.HashMap();
      hints.put(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
      drawGraphics.setRenderingHints(hints);
      drawGraphics.setColor(bgColor);
      drawGraphics.fillRect(0, 0, width, height);
    }
    return drawGraphics;
  }

  public String toString() {
    return String.format("{%s@%d: width: %d, height: %d}",
                         this.getClass().getName(),
                         System.identityHashCode(this), width, height);
  }
}
