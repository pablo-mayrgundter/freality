package space.model;

import org.freality.util.Measure;

/**
 * The Star class represents the physical parameters of a star as well
 * as its rotational period.  NOTE: Our Sun rotates faster at its
 * equator than at its poll.  The parameter used should be the
 * equatorial rotation period.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Star extends Galaxy {
  public Star(String name, String parent,
              double apparentMagnitude, float colorIndex,
              Measure mass, double density,
              Measure meanRadius, Measure siderealRotationPeriod,
              double axialInclination, ObservedLocation loc) {
    super(name, parent, apparentMagnitude, colorIndex, mass, density,
          meanRadius, siderealRotationPeriod, axialInclination, loc);
  }
}
