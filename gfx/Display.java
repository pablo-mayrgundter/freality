package gfx;

import gfx.FullScreenableFrame;

public abstract class Display {

  public static interface Renderer {
    public void render(final Display d);
  }

  final Renderer renderer;
  final FullScreenableFrame frame;

  /**
   * @param renderer The Renderer that is invoked on each frame to
   * animate the scene.  May be null if no application animation is
   * required.
   */
  Display(final Renderer renderer) {
    this.renderer = renderer;
    frame = new FullScreenableFrame();
  }

  public void render() {
    if (renderer != null)
      renderer.render(this);
  }

  public int getFrameWidth() {
    return frame.getWidth();
  }

  public int getFrameHeight() {
    return frame.getHeight();
  }

  public void setVisible() {
    frame.setVisible(true);
  }
}