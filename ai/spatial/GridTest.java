package ai.spatial;

import ai.*;
import gfx.vt.VT100;

final class GridTest extends Test {

  static class Coord implements Comparable {
    final int col;
    final int row;
    Coord(final int col, final int row) {
      this.col = col;
      this.row = row;
    }
    public int compareTo(final Object o) {
      final Coord coord = (Coord)o;
      return this.col - coord.col + this.row - coord.row;
    }
  }

  static class TestLearner extends LinearLearner<Coord> {
    public double differenceFromGoal() {
      return -1;
    }
    public Coord learn(final Coord input) {
      int dCols = 0;
      int dRows = 0;
      double d = 2.0;
      if (goal.col > input.col)
        dCols = 1;//(int)(d * Math.random());
      else if (goal.row < input.row)
        dCols = -1;//(int)(-d * Math.random());
      if (goal.row > input.row)
        dRows = 1;//(int)(d * Math.random());
      else if (goal.row < input.row)
        dRows = -1;//(int)(-d * Math.random());
      return new Coord(input.col + dCols, input.row + dRows);
    }
  }

  public static void main (final String [] args) throws Exception {
    int rows = Integer.parseInt(args[0]);
    int cols = Integer.parseInt(args[1]);
    int [][] walls = new int[rows][cols];
    int col = (int)(0.1*cols), row = (int)(0.4*rows);
    Util.p(VT100.CLEAR_SCREEN);
    for (; col < (int)(0.75*cols); col++) {
      walls[row][col] = 1;
      Util.p(VT100.cursorForce(row,col) + ".");
    }
    row = (int)(0.75*rows);
    for (; col < (int)(0.75*cols); col++) {
      walls[row][col] = 1;
      Util.p(VT100.cursorForce(row,col) + ".");
    }
    final TestLearner l = new TestLearner();
    l.setGoal(new Coord(20,40));
    Util.p(VT100.cursorForce(l.getGoal().col, l.getGoal().row) + "G");
    Coord env = new Coord(0,0);
    while (true) {
      final Coord out = l.learn(env);
      if (walls[out.col][out.row] == 1) {
        Util.p(VT100.cursorForce(env.col, env.row) + "1");
        continue;
      }
      env = out;
      Util.p(VT100.cursorForce(env.col, env.row) + "X");
      if (!Util.sleep(100))
        break;
    }
  }
}