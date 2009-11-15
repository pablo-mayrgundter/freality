package vr.cpack.space.model;

import org.freality.util.Measure;

public class Coordinate {

    public final double ra;
    public final double dec;
    public final Measure distance;

    public Coordinate(double ra, double dec, Measure distance) {
        this.ra = ra;
        this.dec = dec;
        this.distance = distance;
    }

    public String toString() {
        return toString(new StringBuffer()).toString();
    }

    public StringBuffer toString(StringBuffer buf) {
        buf.append("{ra: ").append(ra).append(", dec: ").append(dec).append(", distance: ").append(distance).append("}");
        return buf;
    }
}
