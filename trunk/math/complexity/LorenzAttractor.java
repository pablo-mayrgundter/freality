package math.complexity;

class LorenzAttractor implements Runnable {
  float x = -10, y = 10, z = 25;
  public void run() {
    float dX = 0.01f * (10f*(y - x));
    float dY = 0.01f * (28f*x - y - x*z);
    float dZ = 0.01f * (-2.6f*z + x*y);
    x += dX;
    y += dY;
    z += dZ;
  }
  public static void main(final String [] args) throws Exception {
    final LorenzAttractor la = new LorenzAttractor();
    while (true) {
      la.run();
      System.err.printf("%10f %10f %10f\r", la.x, la.y, la.z);
      //      Thread.sleep(100);
    }
  }
}
