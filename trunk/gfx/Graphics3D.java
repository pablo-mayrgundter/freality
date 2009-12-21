package gfx;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.freality.gui.three.Colors;

public class Graphics3D {

  final BranchGroup scene;
  final Map<String,TransformGroup> tgs;
  
  Graphics3D(final BranchGroup scene) {
    this.scene = scene;
    tgs = new HashMap<String,TransformGroup>();
  }

  public void setBackground(final float r, final float g, final float b) {
    final Background bg = new Background(new Color3f(r, g, b));
    bg.setApplicationBounds(scene.getBounds());
    scene.addChild(bg);
  }

  public void addAmbientLight(final float r, final float g, final float b) {
    final Light light = new AmbientLight(true, new Color3f(r, g, b));
    light.setInfluencingBounds(scene.getBounds());
    scene.addChild(light);
  }

  public void addLight(final float r, final float g, final float b,
                       final float x, final float y, final float z,
                       final float linear, final float quadratic, final float exponential) {
    final Light light = new PointLight(new Color3f(r, g, b),
                                       new Point3f(x, y, z),
                                       new Point3f(linear, quadratic, exponential));
    light.setInfluencingBounds(scene.getBounds());
    scene.addChild(light);
  }

  public void addBall(final String name, final double x, final double y, final double z) {
    TransformGroup tg = null;
    tgs.put(name, tg = new Ball(new Point3d(x,y,z), scene.getBounds()));
    scene.addChild(tg);
  }

  final double [] coords = new double[3];
  final Vector3d v = new Vector3d();
  final Transform3D t3d = new Transform3D();
  public void setBall(final String name, final double x, final double y, final double z) {
    final TransformGroup tg = tgs.get(name);
    tg.getTransform(t3d);
    t3d.get(v);
    v.get(coords);
    coords[0] = x;
    coords[1] = y;
    coords[2] = z;
    v.set(coords);
    t3d.set(v);
    tg.setTransform(t3d);
  }

  static class Ball extends TransformGroup {
    static final int DEFAULT_RESOLUTION = 50;
    //    static final Sphere sphere = makeSphere();
    Ball(final Point3d p, final Bounds bounds) {
      setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      final Transform3D t3d = new Transform3D();
      final Vector3d v = new Vector3d();
      final double [] coords = new double[3];
      getTransform(t3d);
      t3d.get(v);
      v.get(coords);
      coords[0] = p.x;
      coords[1] = p.y;
      coords[2] = p.z;
      v.set(coords);
      t3d.set(v);
      setTransform(t3d);
      addChild(makeSphere());
    }
    static Sphere makeSphere() {
      return new Sphere(0.2f, Sphere.GENERATE_NORMALS, DEFAULT_RESOLUTION, makeAppearance());
    }
    static Appearance makeAppearance() {
      final Appearance app = new Appearance();
      final Material m = new Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.BLACK, 88f);
      m.setLightingEnable(true);
      app.setMaterial(m);
      app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.25f));
      return app;
    }
  }
}