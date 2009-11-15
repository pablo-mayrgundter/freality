package vr.cpack.space.data;

import vr.cpack.space.model.CelestialBody;
import vr.cpack.space.model.Coordinate;
import vr.cpack.space.model.Epoch;
import vr.cpack.space.model.ObservedLocation;
import vr.cpack.space.model.Star;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.freality.util.Measure;

/**
 * Importer for the HYG database.  http://www.astronexus.com/general/data/hyg.php.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
public class HYGLoader {

    /**
     * Loads only stars for now.
     */
    public static Map<String,Star> parseToBodyMap(final String inputSource) throws MalformedURLException, IOException {
        final InputStream is = new URL(inputSource).openStream();
        final Map<String,Star> m = parseToBodyMap(is);
        is.close();
        return m;
    }

    public static Map<String,Star> parseToBodyMap(final InputStream is) throws IOException {
        final Map<String,Star> stars = new HashMap<String,Star>();
        final BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = r.readLine()) != null) {
            if (line.startsWith("StarID"))
                continue;
            final String [] record = line.split(",");
            if (record.length != 14) {
                System.err.println("Skipping: " + line);
                continue;
            }

            try {

                // StarID,Hip,HD,HR,Gliese,BayerFlamsteed,ProperName,RA,Dec,Distance,Mag,AbsMag,Spectrum,ColorIndex
                // 1,     2,224690, , ,,,0.00025315,-19.49883745,45.662100456621, 9.27,5.97222057420059,K3V         , 0.999

                final int hygID = Integer.parseInt(record[0].trim());
                final String bayerFlamsteed = record[5].trim();
                final String properName = record[6].trim();
                final String name = determineName(bayerFlamsteed, properName, hygID);
                final String parent = "";
                final double ra = Double.parseDouble(record[7]);
                final double dec = Double.parseDouble(record[8]);

                // Convert parsec to meter.
                final Measure distance =
                    new Measure(30.85852 * Double.parseDouble(record[9]),
                                Measure.Unit.LENGTH,
                                //                                Measure.Magnitude.MEGA);
                Measure.Magnitude.TERA);

                final double magnitude = Double.parseDouble(record[10]);
                final double apparentMagnitude = Double.parseDouble(record[11]);
                final String spectrum = record[12].trim();
                final float  colorIndex = (float) (record[13].trim().length() > 0 ? Double.parseDouble(record[13]) : 0.0);
                final double absmagnitude = 0.0;
                final Measure mass = new Measure(0.0, Measure.Unit.MASS);
                final double density = 0.0;
                final Measure meanRadius = new Measure(0.0, Measure.Unit.LENGTH);
                final Measure siderealRotationPeriod = new Measure(0.0, Measure.Unit.TIME);
                final double axialInclination = 0.0;
                final Star star =
                    new Star(name,
                             parent,
                             apparentMagnitude,
                             colorIndex,
                             mass,
                             density,
                             meanRadius,
                             siderealRotationPeriod,
                             axialInclination,
                             new ObservedLocation(new Coordinate(ra, dec, distance), Epoch.J2000));
                stars.put(name, star);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.err.println("Couldn't parse: " + e);
                continue;
            }
        }
        return stars;
    }

    static String determineName(String bayerFlamsteed, String properName, int hygID) {
        String name = null;
        if (properName.length() > 0)
            name = properName;
        else if (bayerFlamsteed.length() > 0)
            name = bayerFlamsteed;
        else
            name = "HYG" + hygID;
        return name;
    }

    public static void main(String [] args) throws Exception {
        final Map parsedBodies = parseToBodyMap(System.in);
        System.err.println("Parsed: " + parsedBodies.size());
    }
}
