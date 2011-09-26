package math;

import gfx.FullScreenableFrame;
import math.complexity.*;

import java.awt.Image;
import java.util.Random;

import javax.swing.JFrame;

/**
 * Some good ones:
 *
 *  java -Dfs=true -Xmx200m -Dseed=1316996499948 -Diterations=100000000 -Doversample=3 -Dscale=2 math/Explorer
 *
 * @author Pablo Mayrgundter
 */
class Explorer {

  static final int ITERATIONS = Integer.parseInt(System.getProperty("iterations", "1000000"));
  static final int NUM_PROCESSORS = Integer.parseInt(System.getProperty("cpus", "2"));
  static final int NUM_FRAMES = Integer.parseInt(System.getProperty("frames", "10"));
  static final int OVERSAMPLE = Integer.parseInt(System.getProperty("oversample", "1"));
  static long SEED = Long.parseLong(System.getProperty("seed", System.currentTimeMillis() + ""));

  public static void main(final String [] args) throws Exception {
    System.out.println("random seed: " + SEED);

    final FullScreenableFrame frame = new FullScreenableFrame();
    final Grapher grapher = new Grapher(frame.getWidth() * OVERSAMPLE,
                                        frame.getHeight() * OVERSAMPLE);
    grapher.clear();

    final Explorer ex = new Explorer();
    final ChaosGame flame = new ChaosGame(grapher, ITERATIONS, 1f, new Random(SEED));
    final Drawer drawer = new Drawer(frame.getWidth(),
                                     frame.getHeight(),
                                     OVERSAMPLE);

    drawer.clear();

    final Counter numRunning = new Counter(2);
    for (int i = 0; i < NUM_PROCESSORS; i++) {
      new Thread(new Runnable() {
          public void run() {
            flame.run();
            synchronized(numRunning) {
              numRunning.val--;
              numRunning.notify();
            }
          }
        }).start();
    }
    synchronized(numRunning) {
      while (numRunning.val > 0) {
        numRunning.wait();
      }
    }

    System.out.println(flame.getFunctionXml());
    final Image image = drawer.render(grapher);
    ((java.awt.Graphics2D)frame.getGraphics()).drawImage(image,0,0,null);
    System.out.println("Done.");
  }

  static class Counter {
    int val;
    Counter(int val) {
      this.val = val;
    }
  }
}

/*
java -ea -server -Xmx200m -Dseed=1240544588416 -Diterations=1000000 -Doversample=3 -Dscale=2 -Dxoff=-800 -Dyoff=-800 -Dfunc=13 math/Explorer
random seed: 1240544588416
Palette length: 10
{1=<flame><xform weight="0.500000" color="1" Ex="1"
       coeffs="0.205284 0.563801 -0.629190 0.684326 -0.536352 -0.832039"/>
</flame>, 0=<flame><xform weight="0.500000" color="0" Ex="1"
       coeffs="0.613037 0.345302 -0.303323 0.418577 -0.260114 0.712396"/>
</flame>}
 */