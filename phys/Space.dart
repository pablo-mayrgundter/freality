part of phys;

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
      ndx += val * Math.pow(radius, d);
    }
    return ndx;
  }

  static int wrap(final int radius, int x) {
    x %= radius;
    return x < 0 ? radius + x : x;
  }

  /**
   * One-dimensional storage for variable-dimension space.
   */
  final List<int> space;
  List<int> pos;
  int dimension = 0;
  int radius = 0;

  Space(int this.dimension, int this.radius) :
  space = new List<int>(),
    pos = new List<int>() {
      space.length = Math.pow(radius, dimension);
      pos.length = dimension;
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

  void debug(msg) {
    StringBuffer s = new StringBuffer();
/*
    String prev = query('#info').text;
    s.add(prev);
    s.add("\n");
    s.add(msg);
    query('#info').text = s.toString();
*/
  }

  int get(final List<int> coord) {
    debug(coord);
    return space[coordToNdx(radius, coord)];
  }

  void setCur(final int val) {
    set(val, pos);
  }

  void set(final int val, final List<int> coord) {
    space[coordToNdx(radius, coord)] = val;
  }
}
