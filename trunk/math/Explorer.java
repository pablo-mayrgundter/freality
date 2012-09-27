package math;

import gfx.FullScreenableFrame;

import math.complexity.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import util.Flags;

/**
 * Some good ones:
 *
 *  java -Dfs=true -Xmx200m -Dseed=1316996499948 -Ditr=100000000 -Doversample=3 -Dscale=2 math/Explorer
 *
 * @author Pablo Mayrgundter
 */
public class Explorer extends FullScreenableFrame {

  static Flags flags = new Flags(Explorer.class);
  static final int ITERATIONS = flags.get("iterations", "itr", 1000000);
  static final int NUM_FRAMES = flags.get("frames", "frames", 1);
  static long SEED = flags.get("seed", "seed", System.currentTimeMillis());
  static final int OVERSAMPLE = flags.get("oversample", "oversample", 1);
  static final int SCALE = flags.get("scale", "scale", 1);
  static final int NUM_PROCESSORS = flags.get("cpus", 2);
  static final String OUT_FILE = flags.get("out_file", (String)null);

  final Grapher grapher;
  final ChaosGame flame;
  final Drawer drawer;

  public Explorer() {
    grapher = new Grapher(getWidth() * OVERSAMPLE,
                          getHeight() * OVERSAMPLE,
                          SCALE);
    //flame = new Fern(grapher, ITERATIONS / NUM_PROCESSORS, 1f);
    flame = new SierpenskisGasket(grapher, ITERATIONS / NUM_PROCESSORS, 1f);
    //flame = new ChaosGame(grapher, ITERATIONS / NUM_PROCESSORS, 1f, SEED);
    drawer = new Drawer(getWidth(), getHeight(), OVERSAMPLE);
    grapher.setSeed(SEED);
    grapher.init();
  }

  public void run() {

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
          try {
            numRunning.wait();
          } catch (InterruptedException e) {
            break;
          }
        }
      }

      theta += Math.PI * 2.0 / NUM_FRAMES;
      flame.rotate(theta);

      System.out.println(flame.getFunctionXml());
      final Image image = drawer.render(grapher);
      //((java.awt.Graphics2D) getGraphics()).clearRect(0,0, getWidth(), getHeight());
      ((java.awt.Graphics2D) getGraphics()).drawImage(image,0,0,null);

      if (Boolean.getBoolean("obj")) {
        //FileOutputStream fos = new FileOutputStream(new File(SEED + ".obj"));
        //fos.write(grapher.write().array());
      }

      if (Boolean.getBoolean("png")) {
        java.awt.image.BufferedImage bi =
          new BufferedImage(getWidth() * OVERSAMPLE,
                            getHeight() * OVERSAMPLE,
                            BufferedImage.TYPE_INT_ARGB);
        bi.setAccelerationPriority(1);
        grapher.toImage(bi);
        try {
          javax.imageio.ImageIO.write(bi, "PNG", new java.io.File(SEED + ".png"));
        } catch (IOException e) {
          System.err.println(e);
        }
      }
    }
    // Many IFSes don't generate visible output, so explicitly say
    // we're done.
    System.out.println("Done.");
  }

  public static void main(final String [] args) {
    System.out.println("random seed: " + SEED);
    new Explorer().run();
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