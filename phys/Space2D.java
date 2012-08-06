package phys;

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
public class Space2D extends Space {

  public Space2D(final int width) {
    super(2, width);
  }

  public int above() {
    return get(pos[0], pos[1] + 1);
  }

  public int below() {
    return get(pos[0], pos[1] - 1);
  }

  public int right() {
    return get(pos[0] + 1, pos[1]);
  }

  public int left() {
    return get(pos[0] - 1, pos[1]);
  }
}
