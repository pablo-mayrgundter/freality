part of phys;

/**
 * A square grid of integer values with convenience methods for
 * accessing the value of neighbors above, below, left and to the
 * right.
 *
 * TODO(pablo): generalize the convenience methods and push them up
 * into Space.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class Space2D extends Space {

  Space2D(width) : super(2, width) {
  }

  int get2(int x, int y) {
    return get([x, y]);
  }

  int above() {
    return get(pos[0], pos[1] + 1);
  }

  int below() {
    return get(pos[0], pos[1] - 1);
  }

  int right() {
    return get(pos[0] + 1, pos[1]);
  }

  int left() {
    return get(pos[0] - 1, pos[1]);
  }
}
