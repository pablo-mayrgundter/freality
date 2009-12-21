package gfx;

import java.awt.image.BufferStrategy;
import java.awt.Color;
import java.awt.Graphics2D;

public class Display2D extends Display {

  final BufferStrategy strategy;
  Graphics2D graphics;

  /** The default constructor is equivalent to Display(null). */
  public Display2D() {
    this(null);
  }

  public Display2D(final Renderer renderer) {
    super(renderer);
    frame.createBufferStrategy(2);
    this.strategy = frame.getBufferStrategy();
  }

  public Graphics2D getGraphics() {
    return graphics;
  }

  public void render() {
    graphics = (Graphics2D)strategy.getDrawGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, getFrameWidth(), getFrameHeight());
    super.render();
    graphics.dispose();
    strategy.show();
  }
}