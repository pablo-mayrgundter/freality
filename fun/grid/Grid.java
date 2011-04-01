package grid;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class Grid {

  static int fanout = 4;
  static int count = 0;
  final Grid [] neigh;
  final int level;
  Object val;

  Grid (final int level) {
    this.level = level;
    if (level == 1) {
      neigh = null;
    } else {
      neigh = new Grid[fanout];
      for (int i = 0; i < 4; i++)
        neigh[i] = new Grid(level - 1);
    }
    count++;
  }

  static final int XOFF = Integer.parseInt(System.getProperty("xoff", "0"));
  static final int YOFF = Integer.parseInt(System.getProperty("yoff", "0"));
  static final int WIDTH = Integer.parseInt(System.getProperty("width", "800"));
  static final int HEIGHT = Integer.parseInt(System.getProperty("height", "800"));
  static final int SLEEP = Integer.parseInt(System.getProperty("sleep", "30"));
  static final boolean FULLSCREEN = Boolean.parseBoolean(System.getProperty("fs", "false"));
  static final int levels = Integer.parseInt(System.getProperty("levels", "3"));
  static final int size = Integer.parseInt(System.getProperty("size", "10"));
  static final int w = size, h = size;
  static final int [] path = new int[levels];
  static void genPath() {
    for (int i = 0; i < levels; i++) {
      path[i] = (int)(Math.random() * 4.0);
    }
    path[levels-1] = 0;
  }
  static final BufferedImage [] lvlImgs = new BufferedImage[levels];
  static {
    for (int i = 0; i < levels; i++) {
      lvlImgs[i] = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }
  }

  static void setColor(final Graphics g, final float level, final int pathType) {
    final float l = level/levels;
    g.setColor(new Color(0.75f*l + (float)pathType / 32f, 0.5f*l + (float)pathType / 16f, (float)pathType/8f));
  }

  void draw(final int x, final int y, final int pos, int pathType) {
    // Pretraversal draw.
    Graphics g = lvlImgs[level - 1].getGraphics();
    setColor(g, levels - level, 0);
    int off = w * (int)Math.pow(2, level - 1);
    if (pathType > 0 && level < levels) {
      if (path[level - 1] == pos) {
        setColor(g, level, pathType);
        g.fillRect(x - w/2 + off/2, y - h/2 + off/2, off, off);
      } else {
        return;
      }
    }
    // grid oval.
    lvlImgs[0].getGraphics().drawRect(x - w/2 + off/2, y - h/2 + off/2, off, off);

    if (level > 1) {
      off = w * (int)Math.pow(2, level - 1);
      neigh[0].draw(x, y, 0, pathType);
      neigh[1].draw(x + off, y, 1, pathType);
      neigh[3].draw(x, y + off, 2, pathType);
      neigh[2].draw(x + off, y + off, 3, pathType);
    }
  }

  public static void main(final String [] args) {
    final Frame f = new Frame();
    f.setUndecorated(true);
    f.setVisible(true);
    f.setSize(WIDTH, HEIGHT);
    if (FULLSCREEN) {
      final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      final GraphicsDevice device = env.getDefaultScreenDevice();
      final GraphicsConfiguration config = device.getDefaultConfiguration();
      device.setFullScreenWindow(f);
    }
    final Grid grid = new Grid(levels);
    System.out.println("leafs: "+ count);
    final Graphics g2d = f.getGraphics();
    //((Graphics2D)g2d).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    final BufferedImage compositeImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

    // Grid is expensive to draw, render only once.
    //grid.draw(XOFF, YOFF, 0, 0);

    while (true) {
      genPath();
      grid.draw(XOFF, YOFF, 0, 1 + (int)(Math.random() * 7.0));
      for (int i = levels - 1; i >= 0; i--) {
        compositeImg.getGraphics().drawImage(lvlImgs[i], 0, 0, null);
      }
      g2d.drawImage(compositeImg, 0, 0, null);
      try { Thread.sleep(SLEEP); } catch(Exception e) { break; }
    }
  }
}