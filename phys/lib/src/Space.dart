import 'dart:math' as math;

/**
 * A variable dimensional space of integer values.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class Space {

  /**
   * Computes a scalar index quantity from a variable dimensional
   * coordinate into a single dimenional value, interpreted with the
   * given radius of spatial extent.  The dimensionality of the space
   * is determined by the length of the varargs array.  Space is a
   * ring in each dimension, such that under- or over-flow coordinate
   * values (modulo the radius) wrap around.
   */
  static int coordToNdx(final int radius, final List<int> x) {
    int ndx = 0;
    for (int d = 0; d < x.length; d++) {
      int val = wrap(radius, x[d]);
      ndx += val * math.pow(radius, d);
    }
    return ndx;
  }

  /** Optimized for 2-dimensions. */
  static int coordToNdx2(final int radius, final int x, final int y) {
    int ndx = 0;
    ndx += wrap(radius, x);
    ndx += wrap(radius, y) * radius;
    return ndx;
  }

  static int wrap(final int radius, int x) {
    x %= radius;
    return x < 0 ? radius + x : x;
  }

  /**
   * One-dimensional storage for variable-dimension space.
   */
  List<int> space;
  List<int> pos;
  int dimension = 0;
  int radius = 0;

  Space(int this.dimension, int this.radius) {
    int size = math.pow(radius, dimension);
    this.space = new List<int>(size);
    for (int i = 0; i < size; i++) {
      space[i] = 0;
    }
    this.pos = new List<int>(dimension);
  }

  List<int> getBuffer() {
    return space;
  }

  int getRadius() {
    return radius;
  }

  void setPos(final List<int> coord) {
    pos = coord;
  }

  int getCur() {
    return get(pos);
  }

  int get(final List<int> coord) {
    var ndx = coordToNdx(radius, coord);
    return space[ndx];
  }

  // TODO(pmy): better name
  int get2Super(final int x, final int y) {
    var ndx = coordToNdx2(radius, x, y);
    return space[ndx];
  }

  void setCur(final int val) {
    set(val, pos);
  }

  void set(final int val, final List<int> coord) {
    space[coordToNdx(radius, coord)] = val;
  }

  void set2(final int val, final int x, final int y) {
    space[coordToNdx2(radius, x, y)] = val;
  }
}
