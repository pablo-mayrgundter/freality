package space;

import gfx.Display3D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionPathInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import org.freality.util.Measure;
import space.data.SpaceHandler;
import space.model.CelestialBody;
import space.model.Star;
import space.model.Orbit;
import space.model.Planet;
import space.sceneobject.Orbit3D;
import space.sceneobject.Planet3D;
import space.sceneobject.Rings3D;
import space.sceneobject.Star3D;
import space.sceneobject.Stars3D;
import org.freality.gui.three.FlyToBehavior;
import org.freality.gui.three.ViewPlatformGroup;

/**
 * Testing a standalone browser.  Starting it as a scene-graph itself.
 */
public class Browser extends BranchGroup {

  public static void main(final String [] args) {
    final Browser scene = new Browser();
    scene.load();
    final Display3D d3d = new Display3D(scene);
    final FlyToBehavior ftb =
          new FlyToBehavior(d3d.vpg.getTransformGroup(), scene.destinationNodes, scene.getBounds());
    scene.addChild(ftb);
    d3d.setVisible();
  }

  static {
    org.freality.io.loader.java.Handler.register();
  }

  static final String SOLAR_SYSTEM_XML = System.getProperty("planets", "java:vr/cpack/space/data/solarSystem.xml");
  static final String TEXTURE_BASE_URL = System.getProperty("textures", "java:vr/cpack/space/textures");

  final Map<String, ViewPlatform> viewPlatforms;
  final Map<String, TransformGroup> systemTGMap;
  final Node [] destinationNodes;
  SceneScaling scaling = null;
  SpaceHandler.DataAndExtents dae = null;
  Bounds bounds;

  public Browser() {
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    setCapability(TransformGroup.ALLOW_BOUNDS_READ);
    viewPlatforms = new HashMap<String, ViewPlatform>();
    systemTGMap = new HashMap<String, TransformGroup>();
    destinationNodes = new Node[10];
    // setBounds(bounds);
  }

  public Bounds getBounds() {
    // getBounds();
    return bounds;
  }

  /**
   * Load the celestial bodies from a hard-coded XML datasource. This
   * currenly requires planets only, hence the moon is treated as a
   * planet and the sun is handled separately.  This should be
   * collapsed to a single handler of CelestialBody.
   */
  void load() {
    dae = SpaceHandler.parseToBodyMap(SOLAR_SYSTEM_XML);
    bounds = new BoundingSphere(new Point3d(0, 0, 0),
                                dae.largestOrbit * 1.01);

    scaling = new SceneScaling(Measure.Magnitude.MEGA); // Scale everything *down* by 1000.
    int destinationID = 9;
    for (final CelestialBody body : dae.bodies.values()) {
      if (body.name.equalsIgnoreCase("sol")) {
        final TransformGroup sunTG = makeStarGroup((Star)body);
        addChild(sunTG);
        systemTGMap.put(body.name, sunTG);
        destinationNodes[0] = sunTG;
        continue;
      } else if (!body.getClass().equals(Planet.class)) {
        // saturn's rings.
        continue;
      }

      final TransformGroup [] bodyTGs = makePlanetGroup((Planet) body, ((Planet) body).orbit);
      final TransformGroup parentTG = systemTGMap.get(body.parent);
      parentTG.addChild(bodyTGs[0]);

      if (body.parent.equalsIgnoreCase("sol")) {
        if (destinationID > 0)
          destinationNodes[destinationID--] = bodyTGs[1];
        else
          System.err.println("Destination setup overflow!");
      }

      systemTGMap.put(body.name, bodyTGs[1]);
    }

    // Stars.
    addChild(new Stars3D(scaling));
  }

  TransformGroup makeStarGroup(final Star star) {
    final TransformGroup starTG = createConfiguredTG();
    starTG.setBounds(getBounds());
    starTG.addChild(new Star3D(star, scaling, TEXTURE_BASE_URL));

    final PointLight light = new PointLight();
    light.setInfluencingBounds(getBounds());
    starTG.addChild(light);

    return starTG;
  }

  /**
   * Create the subtree for a planet's orbit viz and interpolators
   * and visualization and rotation interpolators.
   *
   * @return Two TransformGroups, the first for the whole subtree,
   * the second for the subtree affected by the OrbitInterpolator,
   * to which moons, etc. will be added.
   */
  TransformGroup [] makePlanetGroup(final Planet planet, final Orbit orbit) {
    final TransformGroup [] retTGs = new TransformGroup[2];
    final TransformGroup planetTG = createConfiguredTG();
    planetTG.setBounds(getBounds());

    // This is the main group holding the whole planet's subtree.
    retTGs[0] = planetTG;

    // Configure orbit first.
    final Transform3D t3d = new Transform3D();
    t3d.setRotation(new AxisAngle4d(Math.cos(Math.PI / 2.0 - Math.toRadians(orbit.inclination)),
                                    Math.sin(Math.PI / 2.0 - Math.toRadians(orbit.inclination)),
                                    0f,
                                    Math.toRadians(orbit.longitudeOfPerihelion)));
    planetTG.setTransform(t3d);

    // Orbit interp.  This is also the source for the orbit viz
    // geometry, so it's created early and added later.
    TransformGroup curChild = createConfiguredTGChild(planetTG);
    final BoundingSphere schedBounds =
      new BoundingSphere(new Point3d(0.0, 0.0, 0.0), (float) scaling.scale(planet.meanRadius).scalar * 10000f);
    final Alpha orbitAlpha = new Alpha();
    orbitAlpha.setIncreasingAlphaDuration((long) orbit.siderealOrbitPeriod.convert(Measure.Magnitude.MILLI).scalar);
    final TransformInterpolator orbitInterp =
      new OrbitInterpolator(orbitAlpha, curChild, new Transform3D(), orbit, 100, scaling);

    // Ellipse viz for orbital path.
    final PositionPathInterpolator castDownRef = ((PositionPathInterpolator) orbitInterp);
    final Point3f [] orbitCoords = new Point3f[castDownRef.getArrayLengths()];
    for (int i = 0; i < orbitCoords.length; i++)
      orbitCoords[i] = new Point3f();
    castDownRef.getPositions(orbitCoords);
    planetTG.addChild(new Orbit3D(orbitCoords));

    // Now add orbit interp. Everything below here will move along
    // the orbital path along with the planet.
    orbitInterp.setSchedulingBounds(schedBounds);
    curChild.addChild(orbitInterp);

    curChild = createConfiguredTGChild(curChild);
    retTGs[1] = curChild;
    curChild = createConfiguredTGChild(curChild);

    // Tilt this new subgroup to the planet's inclination.
    final Transform3D tiltT3d = new Transform3D();
    curChild.getTransform(tiltT3d);
    tiltT3d.rotZ(Math.toRadians(planet.axialInclination));
    curChild.setTransform(tiltT3d);
    curChild = createConfiguredTGChild(curChild);

    // Planet viz.
    curChild.addChild(new Planet3D(planet, scaling, TEXTURE_BASE_URL));

    // Planet rotation.
    final Alpha rotationAlpha = new Alpha();
    rotationAlpha.setIncreasingAlphaDuration((long)(planet.siderealRotationPeriod.scalar));
    // rotationAlpha.setIncreasingAlphaDuration((long)(planet.siderealRotationPeriod.convert(Measure.Magnitude.MILLI).scalar));
    RotationInterpolator rotationInterp =
      new RotationInterpolator(rotationAlpha, curChild, new Transform3D(), 0.0f, (float) Math.PI * 2.0f);
    rotationInterp.setSchedulingBounds(schedBounds);
    curChild.addChild(rotationInterp);

    // Configure orbital elements.

    viewPlatforms.put(planet.name, new ViewPlatform());
        
    return retTGs;
  }

  TransformGroup createConfiguredTG() {
    final TransformGroup tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    tg.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    return tg;
  }

  /**
   * Add a child to the given tg and return a reference to it.
   */
  TransformGroup createConfiguredTGChild(TransformGroup tg) {
    final TransformGroup child = createConfiguredTG();
    tg.addChild(child);
    return child;
  }
}