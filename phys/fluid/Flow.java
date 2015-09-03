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
  static final boolean HORIZ = flags.get("horiz", "horiz", true);
  static final int COLUMNS = flags.get("columns", "cols", 20);
  static final int NUM = flags.get("num", "num", 0);
  static final int SLEEP = flags.get("sleep", -1);
  static final int HEX_SIZE = flags.get("hexSize", "hexSize", 5);
  static final int COMPARE_STEP = flags.get("compare", -1);

  final int cols, rows, hexSize, frameXOffset, frameYOffset;
  Space2D spaceCur, spaceNext;
  Force force;
  FullScreenableFrame frame = null;
  Graphics2D graphics;
  Color [] palette;
  HexGrid hexGrid;
  int stepCount = 0;
  BufferedImage directImg = null;

  Flow(int num, int reqCols) {
    this(num, reqCols, HEX_SIZE);
  }

  /**
   * @param hexSize of hexagons to draw grid with.
   */
  Flow(int num, int reqCols, int hexSize) {
    this.hexSize = hexSize;

    // Setup gfx first to get possible full-screen dimensions.
    if (SCREEN_GFX) {
      // TODO: these aren't currently needed.
      int frameWidth, frameHeight;
      if (FullScreenableFrame.FULL_SCREEN) {
        frame = new FullScreenableFrame(-1, -1, true);
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();
        int cellWidth, cellHeight, numCellsWide, numCellsHigh;
        if (HORIZ && hexSize > 0) {
          cellWidth = HexGridHorizontal.CELL_WIDTH;
          cellHeight = HexGridHorizontal.CELL_HEIGHT;
          numCellsWide = HexGridHorizontal.colsForWidth(hexSize, frameWidth);
          numCellsHigh = HexGridHorizontal.rowsForHeight(hexSize, frameHeight);
          int numCells = Math.min(numCellsWide, numCellsHigh);
          cols = numCells;
          rows = numCells;
          hexGrid = new HexGridHorizontal(cols, rows, hexSize);
        } else if (!HORIZ && hexSize > 0) {
          // TODO: all copied from HORIZ.. incorrect for vert.
          cellWidth = HexGridHorizontal.CELL_WIDTH;
          cellHeight = HexGridHorizontal.CELL_HEIGHT;
          numCellsWide = HexGridHorizontal.colsForWidth(hexSize, frameWidth);
          numCellsHigh = HexGridHorizontal.rowsForHeight(hexSize, frameHeight);
          int numCells = Math.min(numCellsWide, numCellsHigh);
          cols = numCells;
          rows = numCells;
          hexGrid = new HexGridVertical(cols, rows, hexSize);
        } else {
          cellWidth = cellHeight = 1;
          cols = rows = numCellsWide = numCellsHigh = Math.min(frameWidth, frameHeight);
          hexGrid = null;
          directImg = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        }
        frameXOffset = (frameWidth - (numCellsHigh * hexSize * HexGridHorizontal.X_STRIDE)) / 2;
        frameYOffset = (frameHeight - (numCellsHigh * hexSize * HexGridHorizontal.Y_STRIDE)) / 2;
        System.out.printf("numCellsWide: %d, cellWidth: %d, numCellsHigh: %d, cellHeight: %d\n",
                          numCellsWide, cellWidth, numCellsHigh, cellHeight);
        System.out.printf("cellWidth: %d, cellHeight: %d, frameXOffset: %d, frameYOffset: %d\n",
                          cellWidth, cellHeight, frameXOffset, frameYOffset);
      } else {
        cols = rows = reqCols;
        if (HORIZ) {
          hexGrid = new HexGridHorizontal(cols, rows, hexSize);
        } else {
          hexGrid = new HexGridVertical(cols, rows, hexSize);
        }
        frameWidth = hexGrid.img.getWidth();
        frameHeight = hexGrid.img.getHeight();
        frame = new FullScreenableFrame(frameWidth, frameHeight);
        frameXOffset = frameYOffset = 0;
      }
      graphics = frame.getDrawGraphics(Color.BLACK);
      palette = new Color[7];
      for (int i = 0; i < 7; i++) {
        float val = (float)i / 7f;
        palette[i] = new Color(val, val, 1f);
      }
    } else {
      cols = rows = reqCols;
      palette = new Color[7];
      palette[0] = Color.BLACK;
      palette[1] = Color.BLUE;
      palette[2] = Color.GREEN;
      palette[3] = Color.YELLOW;
      palette[4] = Color.RED;
      palette[5] = Color.MAGENTA;
      palette[6] = Color.WHITE;
      graphics = new TextGraphics(cols, rows);
      frameXOffset = frameYOffset = 0;
    }

    spaceCur = new Space2D(cols);
    spaceNext = new Space2D(cols);
    force = new HexForce();

    if (NUM > 0) {
      final java.util.Random r = new java.util.Random();
      for (int i = 0; i < NUM; i++) {
        spaceCur.set(HexForce.DEBUG_ALL, r.nextInt(cols), r.nextInt(rows));
      }
    }
  }

  public void run () {
    graphics.setBackground(Color.BLACK);
    int wallLeft = (int)((float) cols * 0.2f);
    int wallRight = (int)((float) cols * 0.75f);
    int wallLo = (int)((float) rows * 0.25f);
    int wallHi = (int)((float) rows * 0.75f);
    System.out.printf("wallLeft: %d, wallRight: %d, wallLo: %d, wallHi: %d\n",
                      wallLeft, wallRight, wallLo, wallHi);
    int forceRight =
      HexForce.R |
      HexForce.UR |
      HexForce.DR,
      forceLeft =
      HexForce.L |
      HexForce.UL |
      HexForce.DL;

    while (true) {

      force.apply(spaceCur, spaceNext);

      // Left wall radiating continuously to the right, and
      // vice-versa.
      for (int y = wallLo; y < wallHi; y++) {
        spaceNext.set(forceRight, wallLeft, y);
        spaceNext.set(forceLeft, wallRight, y);
      }

      //spaceNext.set(HexForce.DEBUG_ALL, 0, 0);

      draw();

      if (stepCount == COMPARE_STEP) {
        compare();
      }

      Space2D tmp = spaceCur;
      spaceCur = spaceNext;
      spaceNext = tmp;
      stepCount++;

      if (SLEEP >= 0) {
        try { Thread.sleep(SLEEP); } catch (InterruptedException e) { break; }
      }
      if (stepCount % 100 == 0) {
        System.out.print(stepCount + "\r");
      }
    }
  }

  void draw() {
    if (SCREEN_GFX) {
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          final int popCount = util.Bits.popCount(spaceCur.get(x, y));
          if (hexSize == 0) {
            directImg.setRGB(x, y, palette[popCount].getRGB());
          } else {
            hexGrid.next(palette[popCount]);
          }
        }
        if (hexSize > 0) {
          hexGrid.line();
        }
      }
      if (hexSize > 0) {
        graphics.drawImage(hexGrid.img, frameXOffset, frameYOffset, frame);
      } else {
        graphics.drawImage(directImg, frameXOffset, frameYOffset, frame);
      }
      if (hexSize > 0) {
        hexGrid.reset();
      }
    } else {
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          final int popCount = util.Bits.popCount(spaceCur.get(x, y));
          graphics.setBackground(palette[popCount]);
          graphics.drawLine(x, y, x + 1, y + 1);
        }
      }
    }
  }

  void compare() {
    Space other = (Space) load("flow-" + stepCount + ".obj");
    if (other != null) {
      if (!spaceCur.equals(other)) {
        System.out.println(spaceCur.diff(other));
      } else {
        System.out.println("Systems the same at step " + stepCount);
      }
    } else {
      save(spaceCur, "flow-" + stepCount + ".obj");
      System.out.println("Saved system at step " + stepCount);
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
      (os = new ObjectOutputStream(new FileOutputStream(filename))).writeObject(spaceCur);
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
    final Flow f = new Flow(NUM, COLUMNS);
    f.run();
  }
}
