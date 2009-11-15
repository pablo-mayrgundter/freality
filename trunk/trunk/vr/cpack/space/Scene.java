package vr.cpack.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;

import java.util.logging.Logger;

import org.freality.gui.three.Colors;
import org.freality.util.Measure;

import org.xith3d.scenegraph.*;
import org.xith3d.behaviors.Alpha;
import org.xith3d.behaviors.PositionPathInterpolator;
import org.xith3d.behaviors.RotationInterpolator;
import org.xith3d.behaviors.TransformInterpolator;
import org.xith3d.behaviors.impl.AlphaImpl;
import org.xith3d.behaviors.impl.RotationInterpolatorImpl;

import vr.cpack.space.data.SpaceHandler;
import vr.cpack.space.model.CelestialBody;
import vr.cpack.space.model.Star;
import vr.cpack.space.model.Orbit;
import vr.cpack.space.model.Planet;
import vr.cpack.space.sceneobject.Orbit3D;
import vr.cpack.space.sceneobject.Planet3D;
import vr.cpack.space.sceneobject.Rings3D;
import vr.cpack.space.sceneobject.Star3D;
import vr.cpack.space.sceneobject.Stars3D;
import org.xith3d.render.loop.scheduler.Animator;

/**
 * The scene component that holds the visible scene elements, loaded
 * from a hard-coded XML datasource.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class Scene extends org.freality.gui.three.Scene {

    static final Logger LOG = Logger.getLogger(Scene.class.getName());
    static {
        org.freality.io.loader.java.Handler.register();
    }

    /*
    static final String TEXTURE_BASE_URL = System.getProperty("textures", "http://freality.org/library/data/vr.cpack/space/textures");
    static final String PLANETS_URL = System.getProperty("planets", "http://freality.org/library/data/vr.cpack/space/data/solarSystem.xml");
    */
    static final String PLANETS_URL = System.getProperty("planets", "java:vr/cpack/space/data/solarSystem.xml");

    final Map<String, TransformGroup> systemTGMap;
    final Node [] destinationNodes;

    public Scene (final Animator animator) {
        super (new BoundingSphere(new Point3f(0, 0, 0), 150000000000f), animator);
        systemTGMap = new HashMap<String, TransformGroup>();
        destinationNodes = new Node[10];
    }

    SceneScaling scaling = null;
    SpaceHandler.DataAndExtents dae = null;

    /**
     * Load the celestial bodies from a hard-coded XML
     * datasource. This currenly requires planets only, hence the moon
     * is treated as a planet and the sun is handled separately.  This
     * should be collapsed to a single handler of CelestialBody.
     */
    public void load() {

        // Read scene info and compute scaling.
        dae = SpaceHandler.parseToBodyMap(PLANETS_URL);
        final Map<String, ? extends CelestialBody> bodyMap = dae.bodies;
        scaling = new SceneScaling(Measure.Magnitude.GIGA); // Scale everything *down* by a billion.

        // Stars.
        LOG.info("Adding stars...");
        addChild(new Stars3D(scaling));

        // Sun.
        LOG.info("Adding sun...");
        final Star sun = (Star) bodyMap.get("sol");
        final TransformGroup sunTG = makeStarGroup(sun);
        addChild(sunTG);

        systemTGMap.put(sun.name, sunTG);

        destinationNodes[0] = sunTG;
        if (true) return;
        // Planets.
        int count = 0;
        int destinationID = 9;
        for (final String systemName : bodyMap.keySet()) {
            //            if (true) continue;
            final CelestialBody body = (CelestialBody) bodyMap.get(systemName);
            if (body.name.equalsIgnoreCase("sol") || (!body.getClass().equals(Planet.class)))
                continue;

            final TransformGroup [] bodyTGs = makePlanetGroup((Planet) body, ((Planet) body).orbit);

            final TransformGroup parentTG = (TransformGroup) systemTGMap.get(body.parent);

            if (parentTG == null) {
                LOG.warning("adding " + body.name + " to ROOT SCENE");
                addChild(bodyTGs[0]); // to root scene.
            } else {
                LOG.info(body.parent +"->"+ body.name);
                parentTG.addChild(bodyTGs[0]);
            }

            if (body.parent.equalsIgnoreCase("sol")) {
                if (destinationID > 0)
                    destinationNodes[destinationID--] = bodyTGs[1];
                else
                    System.err.println("Destination setup overflow!");
            }

            systemTGMap.put(body.name, bodyTGs[1]);
        }
    }

    TransformGroup makeStarGroup(final Star star) {

        final TransformGroup starTG = new TransformGroup();

        final Bounds b = new BoundingSphere(new Point3f(0, 0, 0),
                                            (float) dae.largestOrbit * 1000000000.001f); // Just a bit larger to make sure Pluto is fully lit.
        starTG.setBounds(b);
        starTG.addChild(new Star3D(star, scaling));

        final PointLight light = new PointLight();
        light.setEnabled(true);
        light.setInfluencingBounds(b);
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
    TransformGroup [] makePlanetGroup(Planet planet, Orbit orbit) {

        final TransformGroup [] retTGs = new TransformGroup[2];
        final TransformGroup planetTG = new TransformGroup();
        planetTG.setBounds(getBounds());

        // This is the main group holding the whole planet's subtree.
        retTGs[0] = planetTG;

        // Configure orbit first.
        final Transform3D t3d = new Transform3D();
        t3d.setRotation(new AxisAngle4f((float) Math.cos(Math.PI / 2.0 - Math.toRadians(orbit.inclination)),
                                        (float) Math.sin(Math.PI / 2.0 - Math.toRadians(orbit.inclination)),
                                        0f,
                                        (float) Math.toRadians(orbit.longitudeOfPerihelion)));
        planetTG.setTransform(t3d);

        // Orbit interp.  This is also the source for the orbit viz
        // geometry, so it's created early and added later.
        TransformGroup curChild = createConfiguredTGChild(planetTG);
        final BoundingSphere schedBounds =
            new BoundingSphere(new Point3f(), (float) scaling.scale(planet.meanRadius).scalar * 1000000f);
        final Alpha orbitAlpha = new AlphaImpl();
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
        //FIXME        addAnimatableObject(orbitInterp);

        curChild = createConfiguredTGChild(curChild);
        retTGs[1] = curChild;
        curChild = createConfiguredTGChild(curChild);

        // Tilt
        final Transform3D tiltT3d = new Transform3D();
        curChild.getTransform(tiltT3d);
        tiltT3d.rotZ((float) Math.toRadians(planet.axialInclination));
        curChild.setTransform(tiltT3d);
        curChild = createConfiguredTGChild(curChild);

        // Planet viz.
        curChild.addChild(new Planet3D(planet, scaling));

        // Planet rotation.
        final Alpha rotationAlpha = new AlphaImpl();
        rotationAlpha.setIncreasingAlphaDuration((long)(planet.siderealRotationPeriod.scalar));
        long period = (long) planet.siderealRotationPeriod.convert(Measure.Magnitude.DECA).scalar;
        if (period == 0) {
            LOG.warning(planet +" has zero period");
            period = 10;
        }
        System.out.println("rotation period: "+ period);
        rotationAlpha.setIncreasingAlphaDuration(period);
        final RotationInterpolator rotationInterp =
            new RotationInterpolatorImpl(rotationAlpha, curChild, new Transform3D(), 0.001f, (float) Math.PI * 2.0f);
        rotationInterp.setSchedulingBounds(schedBounds);
        // FIXME        addBehavior(rotationInterp);
        // Configure orbital elements.

        return retTGs;
    }

    /**
     * Add a child to the given tg and return a reference to it.
     */
    TransformGroup createConfiguredTGChild(TransformGroup tg) {
        final TransformGroup child = new TransformGroup();
        tg.addChild(child);
        return child;
    }
}
