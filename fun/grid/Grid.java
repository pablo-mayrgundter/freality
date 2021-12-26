package fun.grid;

import gfx.FullScreenableFrame;
import gfx.LayeredImage;
import util.Flags;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;


/**
 * Draws a layered image of random hierarchal squares.
 */
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
    setColor(g, LEVELS - level, 0);
    int off = WIDTH * (int)Math.pow(2, level - 1);
    if (pathType > 0 && level < LEVELS) {
      if (path[level - 1] != pos) {
        return;
      }
      setColor(g, level, pathType);
      g.fillRoundRect(x + off/2, y + off/2, off, off, ROUND, ROUND);
    }

    // Grid draw.
    layers.getGraphics(0).drawRoundRect(x + off/2, y + off/2, off, off, ROUND, ROUND);

    // Recurse.
    if (level > 1) {
      off = WIDTH * (int)Math.pow(2, level - 1);
      neigh[0].draw(x, y, 0, pathType);
      neigh[1].draw(x + off, y, 1, pathType);
      neigh[3].draw(x, y + off, 2, pathType);
      neigh[2].draw(x + off, y + off, 3, pathType);
    }
  }

  void setColor(final Graphics g, final float level, final int pathType) {
    final float l = level/LEVELS;
    // Arbitrary color channel separation.
    g.setColor(new Color(0.75f*l + (float)pathType / 32f,
                         0.5f*l + (float)pathType / 16f,
                         (float)pathType/8f));
  }

  static final int FANOUT = 4;
  static final int LEVELS = Flags.getInt("levels", 8);
  static final int size = Flags.getInt("size", 3);
  static final int WIDTH = size, HEIGHT = size;
  static final int SLEEP = Flags.getInt("sleep", 100);
  static final int COUNT = Flags.getInt("count", -1);
  static final int ROUND = Flags.getInt("round", 20);
  static final int [] path = new int[LEVELS];

  static void genPath() {
    for (int i = 0; i < LEVELS; i++) {
      path[i] = (int)(Math.random() * 4.0);
    }
    path[LEVELS - 1] = 0;
  }


  static boolean run(Grid grid, LayeredImage layers, Graphics gfx, BufferedImage img) {
    genPath();
    grid.draw(0, 0, 0, 1 + (int)(Math.random() * 7.0));
    layers.draw(img.getGraphics());
    gfx.drawImage(img, 0, 0, null);
    try { Thread.sleep(SLEEP); } catch(Exception e) { return false; }
    return true;
  }

  public static void main(final String [] args) {
    final FullScreenableFrame f = new FullScreenableFrame();
    System.out.println(Flags.toStr());
    final LayeredImage layers = new LayeredImage(LEVELS, f.getWidth(), f.getHeight());
    final Grid grid = new Grid(LEVELS, layers);
    final Graphics gfx = f.getDrawGraphics();
    final BufferedImage compositeImg =
      new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);
    // Grid is expensive to draw, render only once.
    if (Boolean.getBoolean("grid")) {
      grid.draw(0, 0, 0, 0);
    }

    if (COUNT > 0) {
      for (int i = 0; i < COUNT; i++)
        if (!run(grid, layers, gfx, compositeImg)) break;
    } else {
      while (true)
        if (!run(grid, layers, gfx, compositeImg)) break;
    }
  }
}
