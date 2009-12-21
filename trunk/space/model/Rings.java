package space.model;

import org.freality.util.Measure;

/**
 * Planetary Rings.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Rings extends CelestialBody {

  /**
   * Semi-major axis of inner rings relative to center of planet.
   */
  public final Measure innerSemiMajorAxis;

  /**
   * Semi-major axis of outer rings relative to center of planet.
   */
  public final Measure outerSemiMajorAxis;

  /**
   * Eccentricity of the ring system.  Are planetary rings always
   * circular?
   */
  public final double eccentricity;

  public Rings(String name, String parent,
               Measure innerSemiMajorAxis, Measure outerSemiMajorAxis) {
    this(name, parent, innerSemiMajorAxis, outerSemiMajorAxis, 0.0);
  }

  public Rings(String name, String parent,
               Measure innerSemiMajorAxis, Measure outerSemiMajorAxis,
               double eccentricity) {
    super(name, parent);
    this.innerSemiMajorAxis = innerSemiMajorAxis;
    this.outerSemiMajorAxis = outerSemiMajorAxis;
    this.eccentricity = eccentricity;
  }

  public void toString(StringBuffer buf) {
    super.toString(buf);
    buf.append("{innerSemiMajorAxis: ").append(innerSemiMajorAxis).append("}\n");
    buf.append("{outerSemiMajorAxis: ").append(outerSemiMajorAxis).append("}\n");
    buf.append("{eccentricity:       ").append(eccentricity).append("}");
  }
}
