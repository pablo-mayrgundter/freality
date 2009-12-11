package phys;

class Phys {
  static void doMotion(final Blob [] blobs) {
    for (final Blob b : blobs) {
      b.coord.x += b.velocity.x;
      b.coord.y += b.velocity.y;
      b.coord.z += b.velocity.z;
    }
  }
}
