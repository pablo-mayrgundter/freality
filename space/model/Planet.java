package space.model;

import org.freality.util.Measure;

/**
 * The Planet class represents the physical parameters of a solar system body.
 * Source: <a href="http://ssd.jpl.nasa.gov/phys_props_planets.html">JPL</a>.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class Planet extends Star {

  public final Color color;

  /** Geometric Albedo. */
  public final double albedo;

  /** Equatorial gravity in meters/s^2. */
  public final double equatorialGravity;

  /** Escape velocity in kilometers/s. */
  public final double escapeVelocity;

  public final Orbit orbit;

  public final boolean hasAtmosphere;

  public Planet(String name, String parent,
                double apparentMagnitude, Color color,
                Measure mass, double density,
                Measure meanRadius, Measure siderealRotationPeriod,
                double albedo, double equatorialGravity,
                double escapeVelocity, double axialInclination,
                ObservedLocation loc,
                Orbit orbit, boolean hasAtmosphere) {
    super(name, parent, apparentMagnitude, color.toColorIndex(), mass, density,
          meanRadius, siderealRotationPeriod, axialInclination, loc);
    this.color = color;
    this.albedo = albedo;
    this.equatorialGravity = equatorialGravity;
    this.escapeVelocity = escapeVelocity;
    this.orbit = orbit;
    this.hasAtmosphere = hasAtmosphere;
  }

  public void toString(StringBuffer buf) {
    super.toString(buf);
    buf.append('\n');
    buf.append("{albedo:   ").append(albedo).append("}\n");
    buf.append("{equatorialGravity: ").append(equatorialGravity).append("}\n");
    buf.append("{escapeVelocity:    ").append(escapeVelocity).append("}\n");
    buf.append("{orbit:\n\t\t").append(orbit.toString().replaceAll("\n", "\n\t\t")).append("\n}");
  }
}
