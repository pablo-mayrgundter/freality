package phys;

import gfx.FullScreenableFrame;
import java.awt.image.BufferStrategy;
import java.awt.Color;
import java.awt.Graphics2D;

class Display {

  static interface Renderer {
    void draw(Graphics2D g);
  }

  final Renderer renderer;
  final BufferStrategy strategy;
  final int width, height;
  Graphics2D g;

  Display(final Renderer renderer) {
    final FullScreenableFrame f = new FullScreenableFrame();
    this.width = f.getWidth();
    this.height = f.getHeight();
    f.createBufferStrategy(2);
    this.strategy = f.getBufferStrategy();
    this.renderer = renderer;
  }

  public void draw() {
    g = (Graphics2D)strategy.getDrawGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    renderer.draw(g);
    g.dispose();
    strategy.show();
  }
}