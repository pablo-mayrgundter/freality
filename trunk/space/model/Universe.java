package space.model;

/**
 * This class represents our universe.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Universe extends CelestialBody {

  public static final Universe OUR_UNIVERSE = new Universe();

  private Universe() {
    super("Our Universe", null);
  }

  protected Universe(String name, String parent) {
    super(name, parent);
  }
}
