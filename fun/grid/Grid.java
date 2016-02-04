package fun.grid;

import gfx.FullScreenableFrame;
import gfx.LayeredImage;
import util.Flags;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class Grid {

  final Grid [] neigh;
  final int level;
  final LayeredImage layers;

  Grid(final int level, final LayeredImage layers) {
    this.level = level;
    this.layers = layers;
    if (level == 1) {
      neigh = null;
    } else {
      neigh = new Grid[FANOUT];
      for (int i = 0; i < 4; i++) {
        neigh[i] = new Grid(level - 1, layers);
      }
    }
  }

  void draw(final int x, final int y, final int pos, int pathType) {
    // Fill draw.
    Graphics g = layers.getGraphics(level - 1);
    setColor(g, levels - level, 0);
    int off = w * (int)Math.pow(2, level - 1);
    if (pathType > 0 && level < levels) {
      if (path[level - 1] != pos) {
        return;
      }
      setColor(g, level, pathType);
      g.fillRect(x - w/2 + off/2, y - h/2 + off/2, off, off);
    }

    // Grid draw.
    layers.getGraphics(0).drawRect(x - w/2 + off/2, y - h/2 + off/2, off, off);

    // Recurse.
    if (level > 1) {
      off = w * (int)Math.pow(2, level - 1);
      neigh[0].draw(x, y, 0, pathType);
      neigh[1].draw(x + off, y, 1, pathType);
      neigh[3].draw(x, y + off, 2, pathType);
      neigh[2].draw(x + off, y + off, 3, pathType);
    }
  }

  void setColor(final Graphics g, final float level, final int pathType) {
    final float l = level/levels;
    // Arbitrary color channel separation.
    g.setColor(new Color(0.75f*l + (float)pathType / 32f,
                         0.5f*l + (float)pathType / 16f,
                         (float)pathType/8f));
  }

  static final int FANOUT = 4;
  static final int levels = Flags.getInt("levels", 7);
  static final int size = Flags.getInt("size", 5);
  static final int w = size, h = size;
  static final int SLEEP = Flags.getInt("sleep", 100);
  static final int [] path = new int[levels];
  static void genPath() {
    for (int i = 0; i < levels; i++) {
      path[i] = (int)(Math.random() * 2.0);
    }
    path[levels - 1] = 0;
  }

  public static void main(final String [] args) {
    final FullScreenableFrame f = new FullScreenableFrame();
    System.out.println(f);
    final LayeredImage layers = new LayeredImage(levels, f.getWidth(), f.getHeight());
    final Grid grid = new Grid(levels, layers);
    final Graphics g2d = f.getDrawGraphics();
    final BufferedImage compositeImg =
      new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);
    // Grid is expensive to draw, render only once.
    if (Boolean.getBoolean("grid")) {
      grid.draw(0, 0, 0, 0);
    }

    while (true) {
      genPath();
      grid.draw(0, 0, 0, 1 + (int)(Math.random() * 7.0));
      layers.draw(compositeImg.getGraphics());
      g2d.drawImage(compositeImg, 0, 0, null);
      try { Thread.sleep(SLEEP); } catch(Exception e) { break; }
    }
  }
}
