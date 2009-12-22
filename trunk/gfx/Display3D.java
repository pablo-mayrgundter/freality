package gfx;

import gfx.FullScreenableFrame;
import java.awt.Point;
import java.awt.Toolkit;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Light;
import javax.media.j3d.Locale;
import javax.media.j3d.PointLight;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.freality.gui.three.Colors;
import org.freality.gui.three.DefaultView;
import org.freality.gui.three.ViewPlatformGroup;
import vr.cpack.space.SpaceShipNavigator;

public class Display3D extends Display {

  public final ViewPlatformGroup vpg;
  final BranchGroup scene;
  final DefaultView view;
  final VirtualUniverse universe;
  final Locale locale;
  final Graphics3D graphics;

  public Display3D() {
    this(null);
  }

  public Display3D(final BranchGroup scene) {
    this(scene, null);
  }

  public Display3D(final BranchGroup scene, final Renderer renderer) {
    super(renderer);
    if (scene == null) {
      this.scene = new BranchGroup();
    } else {
      this.scene = scene;
    }

    universe = new VirtualUniverse();
    locale = new Locale(universe);
    view = new DefaultView();
    vpg = new ViewPlatformGroup(view, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

    addHeadlight(vpg);
    view.attachViewPlatform(vpg.getViewPlatform());
    this.scene.addChild(vpg);

    final SpaceShipNavigator nav =
      new SpaceShipNavigator(vpg.getTransformGroup(), view, vpg.getBounds());

    graphics = new Graphics3D(this.scene);
  }

  void addHeadlight(final ViewPlatformGroup vpg) {
    final PointLight light = new PointLight(Colors.WHITE, new Point3f(0,2,0),
                                            new Point3f(0,0.2f,0.01f));
    light.setInfluencingBounds(vpg.getBounds());
    vpg.getTransformGroup().addChild(light);
  }

  // TODO(pablo): merge these?
  public Graphics3D getGraphics() {
    return graphics;
  }
  public BranchGroup getScene() {
    return scene;
  }
  public ViewPlatformGroup getViewPlatformGroup() {
    return vpg;
  }

  public void setVisible() {
    locale.addBranchGraph(scene);

    final Canvas3D canvas = view.getCanvas3D();
    frame.getContentPane().add(canvas);

    final Toolkit tk = Toolkit.getDefaultToolkit();
    frame.getContentPane().setCursor(tk.createCustomCursor(tk.createImage(new byte[]{}),
                                                           new Point(0, 0), "Pointer"));
    super.setVisible();
  }
}