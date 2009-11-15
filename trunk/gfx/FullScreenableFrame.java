package gfx;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;

public class FullScreenableFrame extends JFrame {

  static final int WIDTH = Integer.parseInt(System.getProperty("width", "400"));
  static final int HEIGHT = Integer.parseInt(System.getProperty("height", "400"));
  static final Boolean FULLSCREEN = Boolean.getBoolean("fs");

  int width, height;

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
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}