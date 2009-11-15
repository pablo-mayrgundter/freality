package vr.cpack.space.sceneobject;

import java.net.URL;
import org.xith3d.scenegraph.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import org.freality.gui.three.Colors;
import org.freality.gui.three.AppearanceUtil;

/**
 * Visualization of planetery rings.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class Rings3D extends Shape3D {

    static final int DEFAULT_RESOLUTION = Integer.parseInt(System.getProperty("res", "50"));
    static final String TEXTURE_BASE_URL = System.getProperty("textures", "http://freality.org/library/data/vr.cpack/space/textures");
    static Material material = new Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.BLACK, 100f);

    public Rings3D(float innerRadius, float outerRadius) {
        this(innerRadius, outerRadius, DEFAULT_RESOLUTION);
    }

    Rings3D(float innerRadius, float outerRadius, int resolution) {
        super(makeArray(innerRadius, outerRadius, resolution), makeApp());
    }

    /**
     * Creates a ring of points using the specified inner and outer
     * radii and assuming the rings are circular.  Equivalent to
     * calling makeArray(innerRadius, outerRadius, 0f, 0f, resolution).
     */
    static final GeometryStripArray makeArray(final float innerRadius, final float outerRadius,
                                              final int resolution) {
        return makeArray(innerRadius, outerRadius, 0f, 0f, resolution);
    }

    /**
     * Creates a ring of points perpendicular to the y-axis using the
     * specified inner and outer ellipses.  Alternating points from
     * each ellipse define the vertices of the strip.
     */
    static final GeometryStripArray makeArray(final float innerRadius, final float outerRadius,
                                              final float innerEccentricity, final float outerEccentricity,
                                              final int resolution) {

        final Point3f [] innerPoints = makeCirclePoints(innerRadius, innerEccentricity, resolution);
        final Point3f [] outerPoints = makeCirclePoints(outerRadius, outerEccentricity, resolution);

        // For resolutions of 1, 2, 3, 4, ..., vertexCount is 4, 6, 8, 10, ...
        final int vertextCount = 2 * innerPoints.length;
        final TriangleStripArray ta = new TriangleStripArray(vertextCount,
                                                             TriangleStripArray.COORDINATES | TriangleStripArray.TEXTURE_COORDINATE_2,
                                                             new int[]{vertextCount});

        boolean toggle = true;
        final TexCoord2f tc = new TexCoord2f();
        //        for (int ndx = 0; ndx < innerPoints.length; ndx++) {
        for (int ndx = 0; ndx < innerPoints.length; ndx++) {
            final Point3f i = innerPoints[ndx];
            final Point3f o = outerPoints[ndx];
            ta.setCoordinate(2 * ndx, i);
            ta.setCoordinate(2 * ndx + 1, o);
            if (toggle) {
                tc.set(0f, 1f);
                ta.setTextureCoordinate(0, 2 * ndx, tc);
                tc.set(1f, 0f);
                ta.setTextureCoordinate(0, 2 * ndx + 1, tc);
            } else {
                tc.set(0f, 1f);
                ta.setTextureCoordinate(0, 2 * ndx, tc);
                tc.set(1f, 1f);
                ta.setTextureCoordinate(0, 2 * ndx + 1, tc);
            }
            toggle = !toggle;
        }

        return ta;
    }

    /**
     * Compute the points of an ellipse of the given resolution,
     * radius and eccentricity in the first quadrant of the X,Z plane.
     *
     * @param r Radius.
     * @param e Eccentricity.
     */
    static final Point3f [] makeCirclePoints(final float r, final float e, final int resolution) {
        final Point3f [] points = new Point3f[resolution + 1];
        final double increment = Math.PI * 2.0 / (double) (resolution);
        final double a = r;
        final double b = a * Math.sqrt(1.0 - Math.pow(e, 2.0));
        double ordinate = 0f;
        for (int i = 0; i <= resolution; i++) {
            final float x = (float) near(0.0, (a * Math.cos(ordinate)));
            final float z = (float) near(0.0, (b * Math.sin(ordinate)));
            points[i] = new Point3f(x, 0f, z);
            ordinate += increment;
        }
        return points;
    }

    static final double near(final double target, final double actual) {
        if (Math.abs(target - actual) < 0.00001)
            return target;
        return actual;
    }

    static final Appearance makeApp() {
        final Appearance appearance = AppearanceUtil.makeAppearance("rings.png");

        appearance.setLineAttributes(new LineAttributes(1f, LineAttributes.PATTERN_SOLID, true));
        final PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setBackFaceNormalFlip(true);
        pa.setCullFace(org.xith3d.scenegraph.PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(pa);

        return appearance;
    }
}
