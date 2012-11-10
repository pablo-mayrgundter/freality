package math;

public final class Point {

  public double x, y, z;

  public Point() {
    this(0, 0, 0);
  }

  public Point(final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Object clone() {
    return new Point(x, y, z);
  }

  public String toString() {
    return x + "," + y + "," + z;
  }
}
