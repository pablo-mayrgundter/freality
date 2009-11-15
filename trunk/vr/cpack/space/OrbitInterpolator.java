package vr.cpack.space;

import vr.cpack.space.model.Orbit;
import org.xith3d.behaviors.Alpha;
import org.xith3d.behaviors.impl.AlphaImpl;
import org.xith3d.behaviors.impl.PositionPathInterpolatorImpl;
import org.xith3d.scenegraph.*;
import javax.vecmath.Point3f;

/**
 * An eliptical orbit interpolator.  Currently interpolates
 * symmetrically around the orbit.  This is not correct, as Kepler's
 * second law says equal areas of the elipse should be swept out in
 * equal times, thus travel is faster near perihelion and slower near
 * aphelion.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
class OrbitInterpolator extends PositionPathInterpolatorImpl {

    /**
     * Constructs an OrbitInterpolator of the given parameters.  The
     * direction of rotation is counter-clockwise, since our galaxy,
     * and the orbits and rotations of things within it are (mostly)
     * all counter-clockwise.
     */
    OrbitInterpolator(final Alpha timeAlpha, final TransformGroup targetTG,
                      final Transform3D axisOfTransform, final Orbit orbit,
                      final int resolution, final SceneScaling scaling) {
        super(timeAlpha, targetTG, axisOfTransform, makeKnots(resolution), makePoints(orbit, resolution, scaling));
        if (resolution < 2)
            throw new IllegalArgumentException("Resolution must be >= 2");
    }

    private static final float [] makeKnots(final int resolution) {
        final float [] knots = new float[resolution];
        final double increment = 1f / (float) resolution;
        float x = 0f;
        for (int i = 0; i < resolution; i++) {
            knots[i] = x;
            x += increment;
        }
        knots[resolution - 1] = 1f; // Can't count on increments to sum to 1.0.
        return knots;
    }

    private static final Point3f [] makePoints(final Orbit orbit, final int resolution, final SceneScaling scaling) {
        final Point3f [] points = new Point3f[resolution];
        final double increment = Math.PI * 2.0 / (float) resolution;
        final double a = scaling.scale(orbit.semiMajorAxis).scalar;
        System.out.println("orbit a: "+ a);
        final double b = a * Math.sqrt(1.0 - Math.pow(orbit.eccentricity, 2.0));
        double x = 0f;
        for (int i = 0; i < resolution; i++) {
            points[i] = new Point3f((float) (a * Math.cos(x)),
                                    0f,
                                    (float) (b * Math.sin(-x)));// Negative for counter-clockwise motion.
            x += increment;
        }
        points[points.length - 1] = new Point3f(points[0]);
        return points;
    }
}
