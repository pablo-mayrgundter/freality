package phys.fluid;

import phys.Force;
import phys.Space2D;

/**
 * Force field on a square grid.  Encode states as a 4-bit number.
 * Mask each bit with following masks to determine if it is inwards
 * (0) or outwards (1).
 *
 * Force masks:
 * 0001 1  <  LEFT
 * 0010 2  >  RIGHT
 * 0100 4  v  DOWN
 * 1000 8  ^  UP
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class GridForce extends Force {

  static final int UP = 1, RIGHT = 2, DOWN = 4, LEFT = 8;

  public void apply(final int radius, final Space2D before, final Space2D after,
                    final int x, final int y) {
    int force = 0;
    force |= RIGHT & before.get(x - 1, y);
    force |= LEFT & before.get(x + 1, y);
    force |= DOWN & before.get(x, y + 1);
    force |= UP & before.get(x, y - 1);
    after.set(force, x, y);
  }
}
