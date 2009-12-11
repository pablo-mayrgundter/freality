package phys;

final class Grav {
  static final float S1 = Float.parseFloat(System.getProperty("s1", "1"));
  static final float MINDIST = Float.parseFloat(System.getProperty("mindist", "1"));
  static void doGrav(final Blob [] blobs) {
    for (int i = 0; i < blobs.length; i++) {
      final Blob a = blobs[i];
      for (int j = 0; j < i; j++) {
        final Blob b = blobs[j];
        float dx = MINDIST + a.coord.x - b.coord.x;
        float dy = MINDIST + a.coord.y - b.coord.y;
        float dz = MINDIST + a.coord.z - b.coord.z;
        float dist2inv = S1/(float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        float dx2 = dx * dist2inv;
        float dy2 = dy * dist2inv;
        float dz2 = dz * dist2inv;
        a.velocity.x -= dx2;
        a.velocity.y -= dy2;
        a.velocity.z -= dz2;
        b.velocity.x += dx2;
        b.velocity.y += dy2;
        b.velocity.z += dz2;
      }
    }
  }
}
