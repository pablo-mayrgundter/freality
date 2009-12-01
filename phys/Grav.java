package phys;

final class Grav {
  static final float S1 = Float.parseFloat(System.getProperty("s1", "1"));
  static final float MINDIST = Float.parseFloat(System.getProperty("mindist", "1"));
  static void doGrav(final Blob [] blobs) {
    for (int i = 0; i < blobs.length; i++) {
      final Blob a = blobs[i];
      float vdx = 0, vdy = 0;
      for (int j = 0; j < i; j++) {
        final Blob b = blobs[j];
        float dx = MINDIST + a.coord.x - b.coord.x;
        float dy = MINDIST + a.coord.y - b.coord.y;
        float dist2inv = S1/(dx*dx + dy*dy);
        float dx2 = dx * dist2inv;
        float dy2 = dy * dist2inv;
        a.velocity.x -= dx2;
        a.velocity.y -= dy2;
        b.velocity.x += dx2;
        b.velocity.y += dy2;
      }
    }
  }
}
