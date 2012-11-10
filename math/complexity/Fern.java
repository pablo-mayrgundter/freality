package math.complexity;

import math.Point;

public class Fern extends ChaosGame {

  public Fern(Grapher g, long iterations, double blend) {
    super(g, iterations, blend, 0);
    Flame [] funcs = new Flame[4];
    funcs[0] = new Linear() {
        public void variation(final Point p) {
          p.x = 0;
          p.y = 0.16f * p.y;
        }
      };
    funcs[1] = new Linear() {
        public void variation(final Point p) {
          p.x = 0.2f * p.x - 0.26f * p.y;
          p.y = 0.23f * p.x + 0.22f * p.y + 1.6f;
        }
      };
    funcs[2] = new Linear() {
        public void variation(final Point p) {
          p.x = -0.15f * p.x + 0.28f * p.y;
          p.y = 0.26f * p.x + 0.24f * p.y + 0.44f;
        }
      };
    funcs[3] = new Linear() {
        public void variation(final Point p) {
          p.x = 0.85f * p.x + 0.04f * p.y;
          p.y = -0.04f * p.x + 0.85f * p.y + 1.6f;
        }
        };

    // Simulate randomized functions by pointing to a given function a
    // percentage of the time.
    mFuncs = new Flame[100];
    for (int i = 0; i < mFuncs.length; i++) {
      Flame f;
      if (i <= 1)  {
        f = funcs[0];
      } else if (i <= 7) {
        f = funcs[1];
      } else if (i <= 14) {
        f = funcs[2];
      } else {
        f = funcs[3];
      }
      mFuncs[i] = f;
    }
  }
}
