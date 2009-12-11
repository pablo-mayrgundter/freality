package org.freality.gui.three;

import gfx.FullScreenableFrame;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Locale;
import javax.media.j3d.VirtualUniverse;

/**
 *  VirtualUniverse -> Locale -> BranchGroups -> TransformGroup ->
 *  ViewPlatform <- View
 */
public class SceneTest {

  protected final BranchGroup scene;
  protected final DefaultView view;
  protected final ViewPlatformGroup vpg;
  protected final VirtualUniverse universe;
  protected final Locale locale;
  final FullScreenableFrame frame;

  public SceneTest(final BranchGroup scene) {
    this.scene = scene;

    view = new DefaultView();

    vpg = new ViewPlatformGroup(view, scene.getBounds());
    view.attachViewPlatform(vpg.getViewPlatform());

    universe = new VirtualUniverse();
    locale = new Locale(universe);

    scene.addChild(vpg);
    frame = new FullScreenableFrame();
  }

  public void makeLive() {
    locale.addBranchGraph(scene);
  }

  public int getFrameWidth() {
    return frame.getWidth();
  }

  public int getFrameHeight() {
    return frame.getHeight();
  }

  public void showScreenFrame() {
    final Canvas3D canvas = view.getCanvas3D();
    final Dimension windowSize;

    frame.getContentPane().add(canvas);

    final Toolkit tk = Toolkit.getDefaultToolkit();
    frame.getContentPane().setCursor(tk.createCustomCursor(tk.createImage(new byte[]{}),
                                                           new Point(0, 0), "Pointer"));
    frame.setVisible(true);
  }

  public static void main(final String [] args) {
    final SimpleScene scene = new SimpleScene();
    final SceneTest t = new SceneTest(scene);
    t.showScreenFrame();
  }
}
