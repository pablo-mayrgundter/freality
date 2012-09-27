package math.complexity;

public class SierpenskisGasket extends ChaosGame {
  public SierpenskisGasket(Grapher g, int iterations, double blend) {
    super(g, iterations, blend, 0);
    Function [] funcs = new Function[3];
    funcs[0] = new Linear() {
        public void variation(final Point p) {
          p.x = p.x / 2f;
          p.y = p.y / 2f;
        }
      };
    funcs[1] = new Linear() {
        public void variation(final Point p) {
          p.x = (p.x + 1f) / 2f;
          p.y = p.y / 2f;
        }
      };
    funcs[2] = new Linear() {
        public void variation(final Point p) {
          p.x = p.x / 2f;
          p.y = (p.y + 1f) / 2f;
        }
      };

    // Simulate randomized functions by pointing to a given function a
    // percentage of the time.
    mFuncs = new Function[3];
    for (int i = 0; i < mFuncs.length; i++) {
      mFuncs[i] = funcs[i];
    }
  }
}
