package ai.spatial;

import ai.*;
import gfx.vt.VT100;

final class GridTest extends Test {
  static class Coord implements Comparable {
    final int x;
    final int y;
    Coord(final int x, final int y) {
      this.x = x;
      this.y = y;
    }
    public int compareTo(final Object o) {
      final Coord coord = (Coord)o;
      return this.x - coord.x + this.y - coord.y;
    }
  }
  static class SpatialRandomLearner extends RandomLearner<Coord> {
    public void moveTowards() {
      env.set(new Coord(env.get().x - 1, env.get().y - 1));
    }
    public void moveAway() {
      env.set(new Coord(env.get().x + 1, env.get().y + 1));
    }
    public int compareToGoal(final Coord stimulus) {
      return goal.x - stimulus.x + goal.y - stimulus.y;
    }
  }
  public static void main (final String [] args) throws Exception {
    final Environment<Coord> env = new Environment<Coord>();
    final Learner<Coord> l = new SpatialRandomLearner();
    l.risk = 0.01;
    l.goal = new Coord(2,1);
    l.env = env;
    env.set(l.goal);
    Util.p(VT100.CLEAR_SCREEN);
    while (true) {
      Util.p(VT100.cursorForce(l.env.get().x, l.env.get().y)+"X");
      l.learn();
      //      if (!Util.sleep(100))
      //        break;
    }
    
  }
}