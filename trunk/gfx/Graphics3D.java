package gfx;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
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
import org.freality.gui.three.Textures;

public class Graphics3D {

  final BranchGroup scene;
  final Map<String,SceneObject> sceneObjects;
  
  Graphics3D(final BranchGroup scene) {
    this.scene = scene;
    sceneObjects = new HashMap<String,SceneObject>();
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
                       final float constant, final float linear, final float quadratic) {
    final Light light = new PointLight(new Color3f(r, g, b),
                                       new Point3f(x, y, z),
                                       new Point3f(constant, linear, quadratic));
    light.setInfluencingBounds(scene.getBounds());
    scene.addChild(light);
  }

  public void addObject(final String name, final double x, final double y, final double z) {
    addObject(name, makeSphere(1, makeAppearance()), x, y, z);
  }
  public void addObject(final String name, final Group shapeGroup,
                        final double x, final double y, final double z) {
    final SceneObject obj = new SceneObject(shapeGroup, new Point3d(x, y, z));
    obj.setBounds(scene.getBounds());
    sceneObjects.put(name, obj);
    scene.addChild(obj);
  }

  public void moveObject(final String name, final double x, final double y, final double z) {
    final SceneObject obj = sceneObjects.get(name);
    obj.move(x, y, z);
  }

  /**
   * SceneObjects are a group that can be moved around.
   */
  static class SceneObject extends TransformGroup {

    final Transform3D t3d = new Transform3D();
    final Vector3d v = new Vector3d();
    final double [] coords = new double[3];

    SceneObject(final Group innerGroup, final Point3d p) {
      setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      getTransform(t3d);
      t3d.get(v);
      v.get(coords);
      coords[0] = p.x;
      coords[1] = p.y;
      coords[2] = p.z;
      v.set(coords);
      t3d.set(v);
      setTransform(t3d);
      addChild(innerGroup);
    }
    void move(final double x, final double y, final double z) {
      getTransform(t3d);
      t3d.get(v);
      v.get(coords);
      coords[0] = x;
      coords[1] = y;
      coords[2] = z;
      v.set(coords);
      t3d.set(v);
      setTransform(t3d);
    }
  }

  static final int DEFAULT_RESOLUTION = 50;
  static Sphere makeSphere(final float radius, final Appearance app) {
    return new Sphere(radius,
                      Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS,
                      DEFAULT_RESOLUTION, app);
  }

  static Appearance makeAppearance() {
    final Appearance app = new Appearance();
    final Material m = new Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.BLACK, 88f);
    m.setLightingEnable(true);
    app.setMaterial(m);
    app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.25f));
    return app;
  }

  static Appearance makeAppearance(final String textureURL) {
    try {
      return makeAppearance(new java.net.URL(textureURL));
    } catch (final java.net.MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }
  static Appearance makeAppearance(final java.net.URL textureURL) {
    final Appearance app = new Appearance();
    final Material m = new Material();
    m.setLightingEnable(true);
    app.setMaterial(m);
    if (textureURL != null)
      Textures.addTexture(app, textureURL, javax.media.j3d.TextureAttributes.MODULATE);
    return app;
  }
}