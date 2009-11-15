package phys.fluid;

import phys.Space2D;

/**
 * Wolfram models a fluid as particle interactions on a hexagonal
 * field:
 *
 * \ /
 * -x-
 * / \
 *
 * Each cell has incident forces to it at the beginning of each step.
 * The evolution rule specifies how the cell transfers these forces
 * outwards by the end of the step.  Wolfram seems to pick a
 * particular rule which transfers the foces most symmetrically for
 * this configuration, and assumes that the count of incident fields
 * is half, or 3/6.
 *
 * To start simply, I will use a grid:
 *
 *  |
 * -x-
 *  |
 *
 * the possible states are encoded as the OR of 4 possible operations,
 * UP(1), RIGHT(2), DOWN(4), LEFT(8):
 *
 *  3   5   6   9   10  12
 *  ^   ^   v   ^   v   v
 * >x> >x< >x> <x< <x> <x<
 *  ^   v   v   ^   ^   v
 *
 * e.g. state 10 means force going outwards inte the RIGHT | LEFT
 * directions, and then implicitly, force coming inwards from UP and
 * DOWN.  A cell's state is transformed by applying the incident
 * forces of its neighbors.  There should be 2 incoming force vectors
 * and 2 outgoing for each cell at each step.  However, I have not yet
 * figured out how to handle the case of a solid within the flow.. I
 * currently model it as having now outgoing force vectors, but this
 * presents the problem that cells next to the solid may not have the
 * 2:2 in/out ratio guaranteed in the other cells.
 *
 * @author Pablo Mayrgundter
 */
class Flow {

  /**
   * Encode states as a 4-bit number.  Mask each bit with following
   * masks to determine if it is inwards (0) or outwards (1).
   *
   * Masks:
   * 0001 1  |
   * 0010 2  -
   * 0100 4  |
   * 1000 8  -
   *
   * States:
   * 0011 3  /
   * 0101 5  -
   * 0110 6  \
   * 1001 9  \
   * 1010 10 |
   * 1100 12 /
   */
  static final int UP = 1, RIGHT = 2, DOWN = 4, LEFT = 8;
  static final int [] STATES = {3, 5, 6, 9, 10, 12};
  static final String [] ART = new String[13];
  static {
    ART[0] = "#";
    ART[3] = "/";
    ART[5] = "-";
    ART[6] = "\\";
    ART[9] = "\\";
    ART[10] = "|";
    ART[12] = "/";
  }

  final int mWidth, mHeight;
  Space2D mCur, mNext;

  Flow (final int width, final int height) {
    mWidth = width;
    mHeight = height;
    mCur = new Space2D(width, height);
    final java.util.Random r = new java.util.Random();
    for (int y = 0; y < mHeight; y++)
      for (int x = 0; x < mWidth; x++) {
        if (x == mWidth / 2 && y > 5 && y < mHeight - 5) {
          mCur.set(x, y, 0);
          continue;
        }
        //        mCur.set(x, y, STATES[r.nextInt(STATES.length)]);
        mCur.set(x, y, 3);
      }
    mNext = new Space2D(width, height);
  }

  public void run () {
    System.out.print(gfx.vt.VT100.CURSOR_HOME);
    for (int y = 1; y < mHeight - 1; y++) {
      for (int x = 1; x < mWidth - 1; x++) {
        final int curForce = mCur.pos(x, y);
        System.out.print(ART[curForce] == null ? curForce : ART[curForce]);
        if (curForce == 0)
          continue;
        int force = 0;
        force += mCur.up() & DOWN;
        force += mCur.right() & LEFT;
        force += mCur.down() & UP;
        force += mCur.left() & RIGHT;
        mNext.set(x, y, force);
      }
      System.out.println();
    }
    Space2D tmp = mCur;
    mCur = mNext;
    mNext = tmp;
  }

  public static void main (final String [] args) {
    final int width = Integer.parseInt(System.getProperty("width", "80"));
    final int height = Integer.parseInt(System.getProperty("height", "40"));
    final Flow f = new Flow(width, height);
    while (true) {
      f.run();
      //      try { Thread.sleep(500); } catch (InterruptedException e) { break; }
    }
  }
}
