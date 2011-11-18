package space.model;

import org.freality.util.Measure;

/**
 * The Galaxy class represents the physical parameters of a galaxy as
 * well as its equatorial position, axial tilt and rotational period.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Galaxy extends Universe {

  /**
   * Apparent magnitude.
   */
  public final double apparentMagnitude;

  /**
   * Color index on the B-V index scale.
   */
  public final float colorIndex;

  /**
   * Mass in x10^23 kilograms.
   */
  public final Measure mass;

  /**
   * Density in grams/cm^3.
   */
  public final double density;

  /**
   * Mean radius in kilometers.
   */
  public final Measure meanRadius;

  /**
   * Sidereal rotation period in hours.
   */
  public final Measure siderealRotationPeriod;

  /**
   * Degrees tilt of the rotational axis relative to its normal or orbital plane.
   */
  public final double axialInclination;

  /**
   * Observed position.
   */
  public final ObservedLocation location;

  public Galaxy(String name, double apparentMagnitude, float colorIndex,
                Measure mass, double density,
                Measure meanRadius, Measure siderealRotationPeriod, double axialInclination,
                ObservedLocation obsLoc) {
    this(name, Universe.OUR_UNIVERSE.name,
         apparentMagnitude, colorIndex,
         mass, density,
         meanRadius, siderealRotationPeriod, axialInclination,
         obsLoc);
  }

  public Galaxy(String name, String parent, double apparentMagnitude, float colorIndex,
                Measure mass, double density,
                Measure meanRadius, Measure siderealRotationPeriod, double axialInclination,
                ObservedLocation location) {
    super(name, parent);
    this.apparentMagnitude = apparentMagnitude;
    this.colorIndex = colorIndex;
    this.mass = mass;
    this.density = density;
    this.meanRadius = meanRadius;
    this.siderealRotationPeriod = siderealRotationPeriod;
    this.axialInclination = axialInclination;
    this.location = location;
  }

  public void toString(StringBuffer buf) {
    super.toString(buf);
    buf.append("\"apparentMagnitude\": ").append("\"" + apparentMagnitude + "\"");
    buf.append(",\n\"colorIndex\": ").append(colorIndex);
    buf.append(",\n\"mass\": ").append(mass.scalar);
    buf.append(",\n\"density\": ").append(density);
    buf.append(",\n\"radius\": ").append(meanRadius.scalar);
    buf.append(",\n\"siderealRotationPeriod\": ").append(siderealRotationPeriod.scalar);
    buf.append(",\n\"axialInclination\": ").append(axialInclination);
    buf.append(",\n\"location\": ").append(location).append("\n");
  }
}
