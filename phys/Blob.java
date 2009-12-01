package phys;

class Blob {
  static final float SIZE = Float.parseFloat(System.getProperty("size", "10"));
  static final float MAXMASS = Integer.parseInt(System.getProperty("maxmass", "1"));
  Point coord = new Point();
  Point velocity = new Point();
  float mass;
  int radius;
  void setMass(final float mass) {
    this.mass = mass;
  }
}
