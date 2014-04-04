package phys;

import java.util.Arrays;

/**
 * A variable dimensional space of integer values.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
public class Space implements java.io.Serializable {

  /**
   * Computes a scalar index quantity from a variable dimensional
   * coordinate into a single dimenional value, interpreted with the
   * given radius of spatial extent.  The dimensionality of the space
   * is determined by the length of the varargs array.  Space is a
   * ring in each dimension, such that under- or over-flow coordinate
   * values (modulo the radius) wrap around.
   */
  static final int coordToNdx(final int radius, final int ... x) {
    int ndx = 0;
    for (int d = 0; d < x.length; d++) {
      int val = wrap(radius, x[d]);
      ndx += val * Math.pow(radius, d);
    }
    return ndx;
  }

  static final int wrap(final int radius, int x) {
    x %= radius;
    return x < 0 ? radius + x : x;
  }

  /**
   * One-dimensional storage for variable-dimension space.
   */
  final int [] space;
  protected int [] pos;
  protected int dimension;
  protected int radius;

  public boolean equals(Object other) {
    if (!(other instanceof Space))
      return false;
    final Space o = (Space) other;
    return Arrays.equals(this.space, o.space)
      && this.dimension == o.dimension
      && this.radius == o.radius;
  }

  public String diff(final Space other) {
    return String.format("Diff: space(%s), dimension(%s), radius(%s)",
                         util.Diff.diff(this.space, other.space),
                         util.Diff.diff(this.dimension, other.dimension),
                         util.Diff.diff(this.radius, other.radius));
  }

  public Space(final int dimension, final int radius) {
    space = new int[(int) Math.pow(radius, dimension)];
    pos = new int[radius];
    this.dimension = dimension;
    this.radius = radius;
  }

  public int [] getBuffer() {
    return space;
  }

  public int getRadius() {
    return radius;
  }

  public final void setPos(final int ... coord) {
    pos = coord;
  }

  public final int get() {
    return get(pos);
  }

  public final int get(final int ... coord) {
    return space[coordToNdx(radius, coord)];
  }

  public void set(final int val) {
    set(val, pos);
  }

  public void set(final int val, final int ... coord) {
    space[coordToNdx(radius, coord)] = val;
  }

  static final long serialVersionUID = -7380887576176557470L;
}
