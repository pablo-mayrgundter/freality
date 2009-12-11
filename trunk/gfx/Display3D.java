package gfx;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.freality.gui.three.Colors;
import org.freality.gui.three.SceneTest;
import vr.cpack.space.SpaceShipNavigator;

public class Display3D extends SceneTest {
  final Map<String,TransformGroup> tgs;
  
  public Display3D() {
    super(new Scene());
    tgs = new HashMap<String,TransformGroup>();
    final SpaceShipNavigator nav =
      new SpaceShipNavigator(vpg.getTransformGroup(), view, ((Scene)scene).bounds);

    final PointLight light = new PointLight(Colors.WHITE, new Point3f(0,0,0), new Point3f(0,0.2f,0.01f));
    light.setInfluencingBounds(((Scene)scene).bounds);
    vpg.getTransformGroup().addChild(light);
  }

  public void setVisible() {
    makeLive();
    showScreenFrame();
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

  static class Scene extends BranchGroup {
    Bounds bounds;

    Scene() {
      bounds = new BoundingSphere(new Point3d(0,0,0), 100000);
      setBounds(bounds);
      final Background bg = new Background(new Color3f(0.05f,0.05f,0.1f));
      addChild(bg);
      bg.setApplicationBounds(bounds);
      //      addAmbientLight();
      addPointLights();
    }
    void addAmbientLight() {
      final AmbientLight light = new AmbientLight();
      light.setInfluencingBounds(bounds);
      addChild(light);
    }
    void addPointLights() {
      Light light = new PointLight(Colors.BLUE, new Point3f(0,0,0), new Point3f(0.5f,0,0.01f));
      light.setInfluencingBounds(bounds);
      addChild(light);
    }
  }

  static class Ball extends TransformGroup {
    static final int DEFAULT_RESOLUTION = 50;
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