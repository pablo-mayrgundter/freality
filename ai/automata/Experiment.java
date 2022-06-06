package ai.automata;

import java.util.List;
import math.automata.viz.Drawer;
import util.Bits;


/**
 * Usage:
 *   java -Dwidth=$[COLUMNS] -Dheight=$[LINES - 1] ai/automata/Experiment
 */
class Experiment {
  public static void main (final String [] args) throws Exception {
    final int width = Integer.parseInt(System.getProperty("width", "20"));
    final int height  = Math.max(Integer.parseInt(System.getProperty("height", "20")) - 1, 1);
    final PatternMatcher matcher = new LeftRightMatcher(width, height);
    final List<Bits> memory = matcher.getMemory();

    // Setup rules
    for (int row = 0; row < height; row++)
      for (int col = 0; col < width; col++)
        matcher.setRule(row, col, (byte) 30);
    // matcher.setRule(row, col, (byte) (Math.random() * 256.0));

    // And input
    matcher.setupInput();

    final Drawer d = new Drawer(memory);
    matcher.run();
    d.clear();
    d.draw(height);
  }
}
