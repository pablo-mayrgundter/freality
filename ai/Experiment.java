package ai;

import math.automata.viz.Drawer;
import util.Bits;

class Experiment {
  public static void main (final String [] args) throws Exception {
    final int width = Integer.parseInt(System.getProperty("width", "20"));
    final int height  = Integer.parseInt(System.getProperty("height", "20"));
    final PatternMatcher matcher = new LeftRightMatcher(width);
    final Drawer d = new Drawer(matcher.mMem);
    int run = 0, ok = 0;
    final float thresh = Float.parseFloat(args[0]);
    while (true) {
      matcher.setupInput();
      matcher.run();
      ok += matcher.testOutput(1) ? 1 : 0;
      run++;

      d.clear();
      d.draw(2);

      if (run > 10 && ((float)ok / (float)run) > thresh)
        break;
    }
    System.out.printf("%d/%d\n", run, ok);
  }
}