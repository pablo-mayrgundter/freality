package phys;

import java.awt.Graphics2D;
import gfx.Display;
import gfx.Display2D;
import gfx.Display3D;
import gfx.Graphics3D;

final class GravSim implements Runnable, Display.Renderer {

  static final int BLOBS = Integer.parseInt(System.getProperty("num", "100"));
  static final boolean THREE = Boolean.getBoolean("three");

  final Display display;
  final ColoredBlob [] blobs;
  final int width, height, depth;

  GravSim() {
    if (THREE) {
      this.display = new Display3D(null, this);
    } else {
      this.display = new Display2D(this);
    }
    this.width = display.getFrameWidth();
    this.height = display.getFrameHeight();
    this.depth = Math.min(width,height);
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
    if (THREE) {
      final Graphics3D g = ((Display3D)display).getGraphics();
      // dark blue.
      g.setBackground(0.05f, 0.05f, 0.1f);
      // blue light at center.
      g.addLight(0, 0, 1, // color
                 0, 0, 0, // center
                 0, 1, 0); // attenuation: constant.
      for (int i = 0; i < blobs.length; i++) {
        final Blob b = blobs[i];
        g.addObject(i+"", b.coord.x, b.coord.y, b.coord.z);
      }
    }
    display.setVisible();
  }

  void grid(final ColoredBlob [] blobs) {
    float x = 0, y = 0, z = 0;
    float cubeRoot = (float)Math.pow(blobs.length, 0.33);
    float halfCubeRoot = cubeRoot / 2f;
    for (int i = 0; i < blobs.length; i++) {
      final Blob b = blobs[i];
      b.coord.x = x - halfCubeRoot;
      b.coord.y = y - halfCubeRoot;
      b.coord.z = z - halfCubeRoot;
      b.setMass(Blob.MAXMASS);
      x++;
      if (x >= cubeRoot) {
        x = 0;
        y++;
        if (y >= cubeRoot) {
          y = 0;
          z++;
        }
      }
    }
  }

  void randomize(final ColoredBlob [] blobs) {
    float offsetX = 0, offsetY = 0, offsetZ = 0;
    if (!THREE) {
      offsetX = width/2f;
      offsetY = height/2f;
      offsetZ = depth/2f;
    }
    for (final ColoredBlob b : blobs) {
      b.coord.x = offsetX + width * (float)Math.random() - offsetX;
      b.coord.y = offsetY + height * (float)Math.random() - offsetY;
      b.coord.z = offsetZ + depth * (float)Math.random() - offsetZ;
//      b.velocity.x = (float)(Math.random() - 0.5);
//      b.velocity.y = (float)(Math.random() - 0.5);
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
      display.render();
      Phys.doMotion(blobs);
      Grav.doGrav(blobs);
      try { Thread.sleep(SLEEP); } catch(InterruptedException e) { break; }
    }
  }

  public void render(final Display d) {
    if (THREE) {
      for (int i = 0; i < blobs.length; i++) {
        final Blob b = blobs[i];
        ((Display3D)display).getGraphics().moveObject(i+"", b.coord.x, b.coord.y, b.coord.z);
      }
    } else {
      int halfWidth = width/2;
      int halfHeight = height/2;
      final Graphics2D g = ((Display2D)display).getGraphics();
      for (final ColoredBlob b : blobs) {
        g.setColor(b.color);
        int halfRadius = b.radius / 2;
        g.fillOval((int)b.coord.x - halfRadius, (int)b.coord.y - halfRadius, b.radius, b.radius);
      }
    }
  }

  public static void main(final String [] args) {
    final GravSim g = new GravSim();
    g.run();
  }
}
