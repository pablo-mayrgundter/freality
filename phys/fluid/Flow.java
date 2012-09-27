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
 * field (p. 378):
 *
 * \ /
 * -x-
 * / \
 *
 * Each cell has incident forces to it at the beginning of each step.
 * The evolution rule specifies how the cell transfers these forces
 * outwards by the end of the step.  Wolfram seems to pick a
 * particular rule which transfers the forces most symmetrically for
 * this configuration, and assumes that the count of incident fields
 * is half, or 3/6.
 *
 * To start simply, I will use a 2-dimensional square grid:
 *
 *  |
 * -x-
 *  |
 *
 * where each node interacts with 4 neighbors.  At a given step, each
 * node has 4 possible states of incoming force per dimension, for a
 * total of 8 incoming states, e.g. for left-right for is either not
 * coming in or coming in from the left only, from the right only, or
 * from both left and right.  Similarly for the up-down dimension.
 * This set of incoming states is encoded as the disjuntion (OR) of
 * those force vectors.  To progress to the next step, absent force is
 * left as absent force, incoming left is propagated to the right, and
 * similarly right to the left, and in the case of both left and right
 * incoming at once, the force is reflected (or doubly passed
 * through).  The full set of states for left-right, before and after,
 * is represented in this table:
 *
 * <pre>Force propagation state transition rules, with encodings for
 * left-right (similarly for up-down):
 *
 *  IN  | OUT | Code
 *  ----------------
 *  -x- | -x- |   00   None
 *  -x< | <x- |   01   Left
 *  >x- | -x> |   10   Right
 *  >x< | <x> |   11   Left OR Right
 * </pre>
 *
 * Solids within the flow are represented with an additional ...
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
final class Flow {

  static Flags flags = new Flags(Flow.class);
  static final boolean SCREEN_GFX = flags.get("gfx", "gfx", true);
  static final int WIDTH = flags.get("width", "w", 80);
  static final int NUM = flags.get("num", "num", 100);
  static final int SLEEP = flags.get("sleep", 100);
  static final int HEX_SIZE = flags.get("hexSize", 5);
  static final int COMPARE_STEP = flags.get("compare", -1);

  final int radius, gridScale;
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
   * @param gridScale of hexagons, for use with hex grid.
   */
  Flow(int num, int width, int gridScale) {

    this.gridScale = gridScale;

    // Setup gfx first to get possible full-screen dimensions.
    if (SCREEN_GFX) {
      palette = new Color[64];
      for (int i = 0; i < 64; i++) {
        float val = (float)i / 63f;
        palette[i] = new Color(val, val, val);
      }
      radius = width;
      hexGrid = new HexGrid(radius, radius, gridScale);
      frame = new FullScreenableFrame(hexGrid.img.getWidth(),
                                      hexGrid.img.getHeight());
      graphics = frame.getDrawGraphics();
    } else {
      palette = new Color[7];
      palette[0] = Color.BLACK;
      palette[1] = Color.BLUE;
      palette[2] = Color.GREEN;
      palette[3] = Color.YELLOW;
      palette[4] = Color.RED;
      palette[5] = Color.MAGENTA;
      palette[6] = Color.WHITE;
      graphics = new TextGraphics(width, width);
      radius = width;
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
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          final int curForce = space.get(x, y);
          if (SCREEN_GFX) {
            hexGrid.next(palette[curForce]);
          } else {
            int popCount = util.Bits.popCount(curForce);
            graphics.setBackground(palette[popCount]);
            graphics.drawLine(x, y, x + 1, y + 1);
          }
        }
        if (SCREEN_GFX) {
          hexGrid.line();
        }
      }
      if (SCREEN_GFX) {
        hexGrid.reset();
      }

      force.apply(space, next);

      for (int y = 0; y < radius; y++) {
        // Continuous left-to-right feed from the left side.
        next.set(forceRight, 0, y);

        // wall should bounce back.
        if (y > wallLo && y < wallHi) {
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
