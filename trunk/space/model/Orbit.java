package space.model;

import org.freality.util.Measure;

/**
 * The Orbit class represents the geometric parameters of a solar
 * system body with respect to its parent, e.g. a planet to its star.
 *
 * <ul>Sources:
 *
 * <li><a href="http://ssd.jpl.nasa.gov/elem_planets.html">JPL Orbital
 * Dynamics</a><br>
 *
 * <li><a href="http://scienceworld.wolfram.com/physics/topics/CelestialMechanics.html">
 * Eric Weisstein's Celestial Mechanics</a>
 *
 * </ul>
 *
 * <p>NOTE: This class should support direct calculation/verification of
 * Kepler's laws for a given set of oribtal parameters.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Orbit {

  // Surely not right?
  public static final double HOURS_IN_SIDEREAL_YEAR = 365 * 24;

  /**
   * The angle (in the body's orbit plane) between the
   * {@link #longitudeOfAscendingNode &quot;Longitude of the Ascending Node&quot;}
   * line and perihelion measured in the direction of the body's
   * orbit.
   */
  public final double argumentOfPerihelion;

  /**
   * Declination is the angular distance on the celestial sphere north
   * or south of the celestial equator. It is measured along the hour
   * circle passing through the celestial object.
   */
  public final double declination;

  /**
   * The position of the focus as a fraction of the semi-major axis.
   */
  public final double eccentricity;

  /**
   * The angle between the vectors normal to the body's orbit plane
   * and the specified reference plane. Typical reference planes are
   * the ecliptic plane and the equatorial plane (referred to a
   * specific epoch).  I.e. the X-axis rotation.
   */
  public final double inclination;

  /**
   * The sum of the
   * {@link #longitudeOfAscendingNode &quot;Longitude of the Ascending Node&quot;}
   * and the
   * {@link #argumentOfPerihelion &quot;Argument of Perihelion&quot;}.
   */
  public final double longitudeOfPerihelion;

  /**
   * The angle between the reference X-direction (typically the vernal
   * equinox) and the point at which the body passes up (north)
   * through the reference plane. This angle is often referred to as
   * capital omega.
   */
  public final double longitudeOfAscendingNode;

  /**
   * The product of an orbiting body's
   * {@link OrbitMotion#meanMotion &quot;Mean Motion&quot;}
   * and time past perihelion passage.
   */
  public final double meanAnomoly;

  /**
   * The sum of the {@link #meanAnomoly &quot;Mean Anomaly&quot;}
   * and the {@link #longitudeOfPerihelion &quot;Longitude of Perihelion&quot;}.
   */
  public final double meanLongitude;

  /**
   * The angle between the equatorial and orbital planes (or the
   * rotational and orbital poles) of a body. The obliquity of the
   * ecliptic for the Earth is the angle between the equatorial and
   * ecliptic planes.
   */
  public final double obliquity;

  /**
   * The distance, in kilometers, between the orbiting body and the Sun at it's
   * closest approach.
   */
  public final Measure perihelionDistance;

  /**
   * The length of the semi-major axis of an orbit ellipse in meters.
   */
  public final Measure semiMajorAxis;

  /**
   * Sidereal orbit period in years.
   */
  public final Measure siderealOrbitPeriod;

  public Orbit(double argumentOfPerihelion, double declination,
               double eccentricity, double inclination,
               double longitudeOfPerihelion, double longitudeOfAscendingNode,
               double meanAnomoly, double meanLongitude,
               double obliquity, Measure perihelionDistance,
               Measure semiMajorAxis, Measure siderealOrbitPeriod) {
    // Elements.
    this.inclination = inclination;
    this.longitudeOfAscendingNode = longitudeOfAscendingNode;
    this.longitudeOfPerihelion = longitudeOfPerihelion;
    this.semiMajorAxis = semiMajorAxis;
    this.eccentricity = eccentricity;
    this.meanLongitude = meanLongitude;

    // Calculated.
    this.meanAnomoly = meanAnomoly;
    this.declination = declination;
    this.obliquity = obliquity;
    this.perihelionDistance = perihelionDistance;
    this.siderealOrbitPeriod = siderealOrbitPeriod;
    this.argumentOfPerihelion = argumentOfPerihelion;
  }

  public String toString() {
    final StringBuffer buf = new StringBuffer();
    buf.append("{\n\"eccentricity\": ").append(eccentricity);
    buf.append(",\n\"inclination\": ").append(inclination);
    buf.append(",\n\"longitudeOfAscendingNode\": ").append(longitudeOfAscendingNode);
    buf.append(",\n\"longitudeOfPerihelion\": ").append(longitudeOfPerihelion);
    buf.append(",\n\"meanLongitude\": ").append(meanLongitude);
    buf.append(",\n\"semiMajorAxis\": ").append(semiMajorAxis.toUnitScalar());
    buf.append(",\n\"siderealOrbitPeriod\": ").append(siderealOrbitPeriod.toUnitScalar()).append("\n}");
    return buf.toString();
  }
}
