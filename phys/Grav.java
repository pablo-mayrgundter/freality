package phys;

final class Grav {
  static final float S1 = Float.parseFloat(System.getProperty("s1", "0.01"));
  static void doGrav(final Blob [] blobs) {
    for (int i = 0; i < blobs.length; i++) {
      final Blob a = blobs[i];
      float vdx = 0, vdy = 0;
      for (int j = 0; j < i; j++) {
        final Blob b = blobs[j];
        float dx = a.coord.x - b.coord.x;
        float dy = a.coord.y - b.coord.y;
        float dist2inv = S1/(dx*dx + dy*dy);
        float dx2 = dx * dist2inv;
        float dy2 = dy * dist2inv;
        a.velocity.x -= dx2;
        a.velocity.y -= dy2;
        b.velocity.x += dx2;
        b.velocity.y += dy2;
        /*
        double scale = S1 / (dist2 * dist + S2);
        double dxs = dx * scale;
        double dys = dy * scale;
        a.velocity.x -= dxs / a.mass;
        a.velocity.y -= dys / a.mass;
        b.velocity.x += dxs / b.mass;
        b.velocity.y += dys / b.mass;
        */
      }
    }
  }
}
