package math;

import gfx.FullScreenableFrame;

import math.complexity.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

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
  static final int NUM_FRAMES = Integer.parseInt(System.getProperty("frames", "1"));
  static final int OVERSAMPLE = Integer.parseInt(System.getProperty("oversample", "1"));
  static final String OUT_FILE = System.getProperty("out_file", null);
  static final int SCALE = Integer.parseInt(System.getProperty("scale", "1"));
  static long SEED = Long.parseLong(System.getProperty("seed", System.currentTimeMillis() + ""));

  public static void main(final String [] args) throws Exception {
    System.out.println("random seed: " + SEED);

    final FullScreenableFrame frame = new FullScreenableFrame();
    final Grapher grapher = new Grapher(frame.getWidth() * OVERSAMPLE,
                                        frame.getHeight() * OVERSAMPLE,
                                        SCALE);
    final ChaosGame flame = new ChaosGame(grapher, ITERATIONS / NUM_PROCESSORS, 1f);
    final Drawer drawer = new Drawer(frame.getWidth(),
                                     frame.getHeight(),
                                     OVERSAMPLE);

    flame.setSeed(SEED);
    flame.init();
    grapher.setSeed(SEED);
    grapher.init();

    if (Boolean.getBoolean("obj")) {
      //byte [] buf = util.Streams.readFully(new FileInputStream(new File(SEED + ".obj")));
      //fos.write(grapher.write().array());
    }

    if (Boolean.getBoolean("verbose")) {
      System.out.println(flame);
    }

    double theta = 0;
    for (int i = 0; i < NUM_FRAMES; i++) {
      final Counter numRunning = new Counter(2);
      grapher.clear();
      for (int j = 0; j < NUM_PROCESSORS; j++) {
        new Thread(new Runnable() {
            public void run() {
              flame.run();
              synchronized (numRunning) {
                numRunning.val--;
                numRunning.notify();
              }
            }
          }).start();
      }
      synchronized (numRunning) {
        while (numRunning.val > 0) {
          numRunning.wait();
        }
      }

      theta += Math.PI * 2.0 / NUM_FRAMES;
      flame.rotate(theta);

      //System.out.println(flame.getFunctionXml());
      final Image image = drawer.render(grapher);
      //((java.awt.Graphics2D) frame.getGraphics()).clearRect(0,0, frame.getWidth(), frame.getHeight());
      ((java.awt.Graphics2D) frame.getGraphics()).drawImage(image,0,0,null);

      if (Boolean.getBoolean("obj")) {
        //FileOutputStream fos = new FileOutputStream(new File(SEED + ".obj"));
        //fos.write(grapher.write().array());
      }

      if (Boolean.getBoolean("png")) {
        java.awt.image.BufferedImage bi =
          new BufferedImage(frame.getWidth() * OVERSAMPLE,
                            frame.getHeight() * OVERSAMPLE,
                            BufferedImage.TYPE_INT_ARGB);
        bi.setAccelerationPriority(1);
        grapher.toImage(bi);
        javax.imageio.ImageIO.write(bi, "PNG", new java.io.File(SEED + ".png"));
      }
    }
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