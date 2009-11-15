package phys;

import gfx.FullScreenableFrame;
import java.awt.image.BufferStrategy;
import java.awt.Color;
import java.awt.Graphics2D;

final class GravSim implements Runnable {

  static final int NUM = Integer.parseInt(System.getProperty("num", "3"));
  static final float GRAV = Integer.parseInt(System.getProperty("grav", "20"));
  static final float SIZE = Float.parseFloat(System.getProperty("size", "1"));

  final ColoredBlob [] blobs;
  final BufferStrategy strategy;
  Graphics2D g;
  int width, height;
  float scale;

  final class ColoredBlob extends Blob {
    Color color = null;
    void setMass(final float mass) {
      this.mass = mass;
      final float shade = 1f - (float) (mass / (GRAV + Math.random() * 10.0));
      color = new Color(shade, shade, shade);
      radius = (int) (mass * SIZE);
    }
  }

  GravSim(final int width, final int height, final int num, final BufferStrategy strategy) {
    this.width = width;
    this.height = height;
    this.strategy = strategy;
    blobs = new ColoredBlob[num];
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
    if (Boolean.getBoolean("orbital")) {
      orbitals(blobs);
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
      b.setMass(GRAV);
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
      b.setMass((float)(GRAV * Math.random()));
    }
  }

  static final float RADIUS = Float.parseFloat(System.getProperty("radius", "1"));
  void orbitals(final ColoredBlob [] blobs) {
    for (int i = 0; i < blobs.length; i++) {
      final ColoredBlob b = blobs[i];
      b.coord.x = RADIUS * (float)Math.sin((float)(Math.random() - 0.5f)) + width/2f;
      b.coord.y = RADIUS * (float)Math.cos((float)(Math.random() - 0.5f)) + height/2f;
      b.velocity.x = (float)Math.random();
      b.velocity.y = (float)Math.random();
      b.setMass(GRAV);
    }
  }

  void draw(final boolean erase) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    int halfWidth = width/2;
    int halfHeight = height/2;
    for (ColoredBlob b : blobs) {
      g.setColor(b.color);
      int halfRadius = b.radius / 2;
      g.fillOval((int)b.coord.x - halfRadius, (int)b.coord.y - halfRadius, b.radius, b.radius);
    }
  }

  public void run() {
    new Thread(new Runnable() {
        public void run() {
    while (true) {
      g = (Graphics2D)strategy.getDrawGraphics();
      draw(false);
      g.dispose();
      strategy.show();
    }
        }
      }).start();
          while (true) {
            Phys.doMotion(blobs);
            Grav.doGrav(blobs);
          }
  }

  public static void main(final String [] args) {
    final FullScreenableFrame f = new FullScreenableFrame();
    f.createBufferStrategy(2);
    final BufferStrategy strategy = f.getBufferStrategy();
    new Thread(new GravSim(f.getWidth(), f.getHeight(), NUM, strategy)).start();
  }
}
