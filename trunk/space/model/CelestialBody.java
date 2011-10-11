package space.model;

/**
 * Root class for all celestial objects.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class CelestialBody {

  // Shoule make these URLs.  Get it?  *Universal* resource
  // locators? Heheheh.  For now, use hierarchical naming.
  public final String name;
  public final String parent;

  public CelestialBody(String name, String parent) {
    this.name = name;
    this.parent = parent;
  }

  public String toString() {
    final StringBuffer buf = new StringBuffer();
    toString(buf);
    return buf.toString();
  }

  public void toString(StringBuffer buf) {
    if (parent == null || parent.equals("")) {
      buf.append("{\"name\":\"").append(name).append("\"}");
    } else {
      buf.append("{\"name\":\"").append(name).append("\",\n");
      buf.append("\"parent\":\"").append(parent).append("\"}");
    }
  }
}
