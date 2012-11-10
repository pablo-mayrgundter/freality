package math.complexity;

import math.Point;

public class SierpenskisGasket extends ChaosGame {
  public SierpenskisGasket(Grapher g, long iterations, double blend) {
    super(g, iterations, blend, 0);
    mFuncs = new Flame[3];
    mFuncs[0] = new Linear() {
        public void variation(final Point p) {
          p.x = p.x / 2f;
          p.y = p.y / 2f;
        }
      };
    mFuncs[1] = new Linear() {
        public void variation(final Point p) {
          p.x = (p.x + 1f) / 2f;
          p.y = p.y / 2f;
        }
      };
    mFuncs[2] = new Linear() {
        public void variation(final Point p) {
          p.x = p.x / 2f;
          p.y = (p.y + 1f) / 2f;
        }
      };
  }
}
