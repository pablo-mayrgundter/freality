package phys.fluid;

import gfx.FullScreenableFrame;
import gfx.TextGraphics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import phys.Space;
import phys.Space2D;
import phys.Force;
import util.Flags;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Wolfram models a fluid as particle interactions on a hexagonal
 * field (p. 378), where each node interacts with 6 neighbors.
 *
 * \ /
 * -x-
 * / \
 *
 * Each cell has incident forces to it at the beginning of each step.
 * The evolution rule specifies how the cell transfers these forces
 * outwards by the end of the step.  Wolfram seems to pick a
 * particular rule which transfers the forces most symmetrically for
 * this configuration, and ensures the count of active vectors is
 * half, or 3/6.  The configuration of active incoming states is
 * encoded as the disjuntion (OR) of those force vectors.  The
 * outgoing forces are found via a lookup table associated with the
 * disjunctive code.
 *
 * Solids within the flow are represented with special hard-coded
 * force propagation conditionals in the main evaluation loop, though
 * this should be replaced with something more general.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
final class Flow {

  static Flags flags = new Flags(Flow.class);
  static final boolean SCREEN_GFX = flags.get("gfx", "gfx", true);
  static {
    // Set this immediatly before awt initializes.
    if (!SCREEN_GFX)
      System.setProperty("java.awt.headless", "true");
  }
  static final int WIDTH = flags.get("width", "w", 20);
  static final int NUM = flags.get("num", "num", 100);
  static final int SLEEP = flags.get("sleep", 100);
  static final int HEX_SIZE = flags.get("hexSize", 5);
  static final int COMPARE_STEP = flags.get("compare", -1);

  final int radius, hexSize;
  Space2D space, next;
  Force force;
  FullScreenableFrame frame = null;
  Graphics2D graphics;
  Color [] palette;
  HexGrid hexGrid;

  Flow(int num, int width) {
    this(num, width, HEX_SIZE);
  }

  /**
   * @param hexSize of hexagons to draw grid with.
   */
  Flow(int num, int cols, int hexSize) {

    this.hexSize = hexSize;

    // Setup gfx first to get possible full-screen dimensions.
    if (SCREEN_GFX) {
      if (FullScreenableFrame.FULL_SCREEN) {
        frame = new FullScreenableFrame(-1, -1, true);
        // computation related to the sizing in HexGrid.java, not sure
        // why + 2 instead of just say off by one.
        // int cols = (frame.getWidth()) - hexSize) / 3 * hexSize;
        int rows = (frame.getHeight() - (6 * hexSize)) / (4 * hexSize) + 2;
        radius = rows;
        hexGrid = new HexGrid(radius, radius, hexSize);
      } else {
        radius = cols;
        hexGrid = new HexGrid(radius, radius, hexSize);
        frame = new FullScreenableFrame(hexGrid.img.getWidth(),
                                        hexGrid.img.getHeight());
      }
      graphics = frame.getDrawGraphics();
      palette = new Color[7];
      for (int i = 0; i < 7; i++) {
        float val = (float)i / 7f;
        palette[i] = new Color(val, val, val);
      }
    } else {
      palette = new Color[7];
      palette[0] = Color.BLACK;
      palette[1] = Color.BLUE;
      palette[2] = Color.GREEN;
      palette[3] = Color.YELLOW;
      palette[4] = Color.RED;
      palette[5] = Color.MAGENTA;
      palette[6] = Color.WHITE;
      graphics = new TextGraphics(cols, cols);
      radius = cols;
    }

    space = new Space2D(radius);
    next = new Space2D(radius);
    force = new HexForce();

    final java.util.Random r = new java.util.Random();
    if (NUM == -1) {
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          space.set(r.nextInt(64), x, y);
        }
      }
    } else if (NUM > 0) {
      for (int i = 0; i < NUM; i++) {
        space.set(r.nextInt(64), r.nextInt(radius), r.nextInt(radius));
      }
    }
  }

  public void run () {
    graphics.setBackground(Color.BLACK);
    int wallLeft = (int)((float) radius * 0.8f);
    int wallLo = (int)((float) radius * 0.25f);
    int wallHi = (int)((float) radius * 0.75f);
    int forceRight =
      HexForce.R |
      HexForce.UR |
      HexForce.DR,
      forceWall =
      HexForce.L |
      HexForce.UL |
      HexForce.DL;

    int stepCount = 0;
    while (true) {
      if (SCREEN_GFX) {
        for (int y = 0; y < radius; y++) {
          for (int x = 0; x < radius; x++) {
            final int popCount = util.Bits.popCount(space.get(x, y));
            hexGrid.next(palette[popCount]);
          }
          hexGrid.line();
        }
        hexGrid.reset();
      } else {
        for (int y = 0; y < radius; y++) {
          for (int x = 0; x < radius; x++) {
            final int popCount = util.Bits.popCount(space.get(x, y));
            graphics.setBackground(palette[popCount]);
            graphics.drawLine(x, y, x + 1, y + 1);
          }
        }
      }

      force.apply(space, next);

      for (int y = 0; y < radius; y++) {
        // Continuous left-to-right feed from the left side.
        next.set(forceRight, 0, y);

        // wall should bounce back.
        if (y >= wallLo && y <= wallHi) {
          /*
          for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
              if (i == 0 || ) {
                continue;
              }
            }
          }
          */
          next.set(forceWall, wallLeft, y);
        }
      }

      if (stepCount == COMPARE_STEP) {
        Space other = (Space) load("flow-" + stepCount + ".obj");
        if (other != null) {
          if (!space.equals(other)) {
            System.out.println(space.diff(other));
          } else {
            System.out.println("Systems the same at step " + stepCount);
          }
        } else {
          save(this.space, "flow-" + stepCount + ".obj");
          System.out.println("Saved system at step " + stepCount);
        }
      }

      Space2D tmp = space;
      space = next;
      next = tmp;

      if (SCREEN_GFX) {
        graphics.drawImage(hexGrid.img, 0, 0, frame);
      } else {
        graphics.setBackground(Color.WHITE);
      }
      // System.out.println(force);
      try { Thread.sleep(SLEEP); } catch (InterruptedException e) { break; }

      stepCount++;
    }
  }

  Object load(String filename) {
    ObjectInputStream is = null;
    try {
      return (is = new ObjectInputStream(new FileInputStream(filename))).readObject();
    } catch (Exception e) {
      return null;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  void save(Object o, String filename) {
    ObjectOutputStream os = null;
    try {
      (os = new ObjectOutputStream(new FileOutputStream(filename))).writeObject(space);
    } catch (Exception e) {
    throw new RuntimeException(e);
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static void main (final String [] args) {
    final Flow f = new Flow(NUM, WIDTH);
    f.run();
  }
}
