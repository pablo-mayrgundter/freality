package gfx;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import vr.cpack.space.sceneobject.Rings3D;
import org.freality.gui.three.Colors;
import org.freality.gui.three.Textures;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Switch;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import space.SceneScaling;
import space.model.Planet;
import space.sceneobject.Planet3D;
import org.freality.util.Measure;

class Test extends BranchGroup implements Display.Renderer {

  static final String PLANET_NAME = System.getProperty("planet", "earth");
  static {
    org.freality.io.loader.java.Handler.register();
  }

  public static void main(final String [] args) {
    final Test t = new Test();
    final Display3D d3d = new Display3D(t, t);
    t.load(d3d.getGraphics());
    d3d.setVisible();
  }

  Bounds bounds;

  public Test() {
    bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10000.0);
    // setBounds(bounds);
  }

  public void render(final Display d) {}

  public Bounds getBounds() {
    // getBounds();
    return bounds;
  }

  public void load(final Graphics3D g) {
    TransformGroup tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.setBounds(getBounds());

    //    tg = planet(tg);
    tg = planet2(g, tg);

    g.addLight(1, 1, 1, // color: white
               20, 20, 20, // center
               1, 0, 0); // linear attenuation

    final Transform3D zAxis = new Transform3D();
    zAxis.lookAt(new Point3d(0,0,0), new Point3d(1,0,0), new Vector3d(0,1,0));

    addChild(tg);
  }

  static {
    org.freality.io.loader.java.Handler.register();
  }
  TransformGroup planet2(final Graphics3D g, TransformGroup tg) {
    g.addObject("earth", Graphics3D.makeSphere(10, Graphics3D.makeAppearance("java:space/textures/earth.jpg")), 0, 0, 0);
    return tg;
  }

  TransformGroup planet(TransformGroup tg) {
    final Planet p = new Planet(PLANET_NAME, "sol",
                                1, new space.model.Color(1,1,1),
                                null, 0,
                                new Measure(10, Measure.Unit.LENGTH), null,
                                0.12f * 128.0f, 0, // albedo, grav
                                0, 0,
                                null, null, true);
    tg.addChild(new Planet3D(p, new SceneScaling(), "java:space/textures"));

    final Transform3D yAxis = new Transform3D();

    final Alpha alpha = new Alpha();
    alpha.setIncreasingAlphaDuration(60000);
    final BoundingSphere schedBounds =
      new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    TransformInterpolator interp;

    tg = wrap(tg);

    interp = new RotationInterpolator(alpha, tg, yAxis, 0.0f, (float) Math.PI * 2.0f);
    interp.setSchedulingBounds(schedBounds);
    tg.addChild(interp);

    return wrap(tg);
  }

  TransformGroup wrap(TransformGroup tg) {
    final TransformGroup tgWrapper = new TransformGroup();
    tgWrapper.addChild(tg);
    tgWrapper.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tgWrapper.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    return tgWrapper;
  }
}