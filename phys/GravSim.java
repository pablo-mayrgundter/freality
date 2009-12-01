package phys;

import java.awt.Graphics2D;

final class GravSim implements Runnable, Display.Renderer {

  static final int BLOBS = Integer.parseInt(System.getProperty("num", "100"));

  final Display display;
  final ColoredBlob [] blobs;
  final int width, height;

  GravSim() {
    this.display = new Display(this);
    this.width = display.width;
    this.height = display.height;
    blobs = new ColoredBlob[BLOBS];
    for (int i = 0; i < blobs.length; i++)
      blobs[i] = new ColoredBlob();

    boolean rendered = false;
    if (Boolean.getBoolean("random")) {
      randomize(blobs);
      rendered = true;
    }
    if (Boolean.getBoolean("grid")) {
      grid(blobs);
      rendered = true;
    }
    if (Boolean.getBoolean("orbits")) {
      orbits(blobs);
      rendered = true;
    }
    if (!rendered)
      grid(blobs);
    if (Boolean.getBoolean("tracer"))
      blobs[blobs.length - 1].color = new java.awt.Color(0, 0, 1f);
  }

  void grid(final ColoredBlob [] blobs) {
    int x = 0, y = 0,
      xspacing = width / (int) Math.sqrt(blobs.length),
      yspacing = height / (int) Math.sqrt(blobs.length);
    for (final ColoredBlob b : blobs) {
      b.coord.x = x;
      b.coord.y = y;
      b.setMass(Blob.MAXMASS);
      x += xspacing;
      if (x > width) {
        x = 0;
        y += yspacing;
      }
    }
  }

  void randomize(final ColoredBlob [] blobs) {
    for (final ColoredBlob b : blobs) {
      b.coord.x = (int) (width * Math.random());
      b.coord.y = (int) (height * Math.random());
      b.velocity.x = (float)(Math.random() - 0.5);
      b.velocity.y = (float)(Math.random() - 0.5);
      b.setMass((float)(Blob.MAXMASS * Math.random()));
    }
  }

  static final float RADIUS = Float.parseFloat(System.getProperty("radius", "100"));
  static final int RINGS = Integer.parseInt(System.getProperty("rings", "10"));
  static final int RINGSEP = Integer.parseInt(System.getProperty("ringsep", "10"));
  void orbits(final ColoredBlob [] blobs) {
    final float blobsPerRing = BLOBS / RINGS;
    int step = 0;
    float theta = 0, radius = RADIUS, xOff = width/2f, yOff = height/2f;
    for (int i = 0; i < blobs.length; i++) {
      final ColoredBlob b = blobs[i];
      if (i % blobsPerRing == 0) {
        step = 0;
        radius += RINGSEP;
      }
      theta = (float)step / blobsPerRing * (float)Math.PI * 2f;
      b.coord.x = xOff + radius * (float)Math.sin(theta);
      b.coord.y = yOff + radius * (float)Math.cos(theta);
      b.velocity.x = 0;
      b.velocity.y = 0;
      b.setMass(Blob.MAXMASS);
      step++;
    }
  }

  static final int SLEEP = Integer.parseInt(System.getProperty("sleep", "0"));
  public void run() {
    while (true) {
      display.draw();
      Phys.doMotion(blobs);
      Grav.doGrav(blobs);
      try { Thread.sleep(SLEEP); } catch(InterruptedException e) { break; }
    }
  }

  public void draw(final Graphics2D g) {
    int halfWidth = width/2;
    int halfHeight = height/2;
    for (ColoredBlob b : blobs) {
      g.setColor(b.color);
      int halfRadius = b.radius / 2;
      g.fillOval((int)b.coord.x - halfRadius, (int)b.coord.y - halfRadius, b.radius, b.radius);
    }
  }

  public static void main(final String [] args) {
    new GravSim().run();
  }
}
