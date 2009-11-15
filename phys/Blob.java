package phys;

class Blob {
  Point coord = new Point();
  Point velocity = new Point();
  float mass;
  int radius;
  void setMass(final float mass) {
    this.mass = mass;
  }
}
