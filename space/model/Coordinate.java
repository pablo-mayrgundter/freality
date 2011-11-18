package space.model;

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
    buf.append("{\n\"ra\": ").append(ra);
    buf.append(",\n\"dec\": ").append(dec);
    buf.append(",\n\"dist\": ").append(distance.toUnitScalar()).append("\n}");
    return buf;
  }

  static final Coordinate TEST = new Coordinate(1,2,new Measure(3,Measure.Unit.LENGTH));
  public static void main(String [] args) {
    System.out.println(TEST);
  }
}
