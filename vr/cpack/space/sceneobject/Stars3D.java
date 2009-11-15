package vr.cpack.space.sceneobject;

import vr.cpack.space.data.HYGLoader;
import vr.cpack.space.model.CelestialBody;
import vr.cpack.space.model.Color;
import vr.cpack.space.model.Coordinate;
import vr.cpack.space.model.Star;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.vecmath.Color3f;

import org.freality.gui.three.Colors;
import org.freality.gui.three.SceneScaling;
import org.freality.util.Measure;

import org.xith3d.scenegraph.*;

/**
 * This is a short-term solution to integrating stars into the
 * simulation.  This method loads the HYG Star Catalog and adds the
 * stars simply as points in the sky at the (not really) correct
 * position.  What *should* happen is that every CelestialBody should
 * have a "near" and a "far" viz.  When far away, the Sun and planets
 * should also appear as points, and when near to another star, it
 * should (roughly) appears as the Sun.  But, that's for another time.
 */
public class Stars3D extends TransformGroup {

    static final double MIN_MAGNITUDE = 50;

    public Stars3D (final SceneScaling scaling) {

        //        Map<String, ? extends CelestialBody> stars = null;
        Map<String, Star> stars = null;

        try {
            //            final String starURL = System.getProperty("stars", "http://freality.org/library/data/vr.cpack/space/data/hygfull.csv.gz");
            final String starURL = System.getProperty("stars", "java:vr/cpack/space/data/hygfull.csv.gz");
            final GZIPInputStream gzis = new GZIPInputStream(new URL(starURL).openStream());
            stars = HYGLoader.parseToBodyMap(gzis);
            gzis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        double maxMagnitude = MIN_MAGNITUDE;
        // Group stars by magnitudes.
        final Map<MagColor, List<RenderedStar>> starListsByMagAndColor = new HashMap<MagColor, List<RenderedStar>>();
        for (final String starName : stars.keySet()) {
            final Star s = (Star) stars.get(starName);
            final RenderedStar star = new RenderedStar(s);
            if (star.magColor.magnitude > MIN_MAGNITUDE)
                continue;
            if (star.magColor.magnitude > maxMagnitude)
                maxMagnitude = star.magColor.magnitude;
            List<RenderedStar> singleMagList = starListsByMagAndColor.get(star.magColor);
            if (singleMagList == null)
                starListsByMagAndColor.put(star.magColor, singleMagList = new ArrayList<RenderedStar>());
            singleMagList.add(star);
        }

        // Construct point arrays for each magnitude group, with
        // appropriate appearance.
        System.out.println("# star rendering groups: "+ starListsByMagAndColor.keySet().size());
        for (final MagColor magColor : starListsByMagAndColor.keySet()) {
            final List<RenderedStar> singleMagList = starListsByMagAndColor.get(magColor);
            final PointArray points = new PointArray(singleMagList.size(), PointArray.COORDINATES);
            final float [] c = new float[3];
            int count = 0;
            for (final RenderedStar star : singleMagList) {
                final double ra  = Math.toRadians((star.c.ra / 24.0) * 360.0);
                final double dec = Math.toRadians(-star.c.dec + 90.0); // -90 -> 180, 0 -> 90, 90 -> 0, y=-x + 90
                final double distance = scaling.scale(star.c.distance).scalar;
                c[0] = (float) (distance * Math.sin(ra) * Math.sin(dec));
                c[1] = (float) (distance * Math.cos(dec));
                c[2] = (float) (distance * Math.cos(ra) * Math.sin(dec));
                points.setCoordinate(count++, c);
                //                System.out.printf("%s: %.2f, %.2f, %.2f, %.2f, %.2f\n", star.name, ra, dec, c[0], c[1], c[2]);
            }
            addChild(new Shape3D(points, makeAppearance(magColor, (float) maxMagnitude)));
        }
    }

    class MagColor {
        final float magnitude;
        final float colorIndex;
        MagColor(float magnitude, float colorIndex) {
            this.magnitude = magnitude;
            this.colorIndex = colorIndex;
        }
        int magBucket() {
            return (int) (magnitude / 100f);
        }
        int colorBucket() {
            return (int) (colorIndex * 10f);
        }
        public int hashCode() {
            return (int)((float) magBucket()) + colorBucket();
        }
        public boolean equals(Object o) {
            return ((MagColor)o).hashCode() == hashCode();
        }
        public String toString() {
            return "Mag Bucket: " + magBucket() + ", Color Bucket: " + colorBucket() + ", hashCode: " + hashCode();
        }
    }

    // Temporary holder for construction info.
    class RenderedStar {
        final String name;
        final Coordinate c;
        final MagColor magColor;
        RenderedStar(String name, Coordinate c, MagColor magColor) {
            this.name = name;
            this.c = c;
            this.magColor = magColor;
        }
        RenderedStar(Star star) {
            this(star.name, star.location.coordinate, new MagColor((float) star.apparentMagnitude, star.colorIndex));
        }
    }

    /**
     * Make appearance with color and size set for the given magnitude
     * and type.  Color is determined, for lack of a better source,
     * according to this quote "Color index is now defined as the B
     * magnitude minus the V magnitude. A pure white star has a B-V of
     * about 0.2, our yellow Sun is 0.63, orange-red Betelgeuse is
     * 1.85, and the bluest star believed possible is -0.4  pale
     * blue-white."
     * (http://cobalt.golden.net/~kwastro/Stellar%20Magnitude%20System.htm).
     */
    Appearance makeAppearance(MagColor mc, float maxMagnitude) {
        final Appearance appearance = new Appearance();
        final Color c = Color.fromBVColorIndex(mc.colorIndex);

        // Temporary hack assumes mag < 0.
        final float max = Math.abs(maxMagnitude);
        final float mag = Math.abs(mc.magnitude);
        final float delta = max - mag;
        final PointAttributes pa = new PointAttributes(delta * 0.5f, true);
        appearance.setPointAttributes(pa);
        final float brightnessReduction = (4f * mag) / max;
        final ColoringAttributes ca = new ColoringAttributes(new Color3f(c.red * brightnessReduction,
                                                                         c.green * brightnessReduction,
                                                                         c.blue * brightnessReduction),
                                                             ColoringAttributes.NICEST);
        appearance.setColoringAttributes(ca);

        return appearance;
    }
}
