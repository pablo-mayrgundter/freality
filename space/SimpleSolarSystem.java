package space;

import gfx.Display3D;
import javax.media.j3d.BranchGroup;
import org.freality.gui.three.FlyToBehavior;
import org.freality.gui.three.ViewPlatformGroup;

/**
 * This class sets up a simple solar system model.  The Locale for
 * this scene is the center of our (the Milky Way) galaxy, as there is
 * no higher-level "center" to use as a reference.  Other galaxies and
 * Universe objects can be described in relative position to the
 * center of our galaxy.
 *
 * @author Pablo Mayrgundter
 * @version $Revision: 1.1.1.1 $
 */
public class SimpleSolarSystem {

  public SimpleSolarSystem(final BranchGroup scene, final ViewPlatformGroup vpg) {
    final Scene ss = new Scene();
    final FlyToBehavior ftb =
          new FlyToBehavior(vpg.getTransformGroup(), ss.destinationNodes, scene.getBounds());
    scene.addChild(ftb);
    scene.addChild(ss);
  }

  public static void main(final String [] args) {
    final Display3D d3d = new Display3D();
    final SimpleSolarSystem ss = new SimpleSolarSystem(d3d.getScene(), d3d.getViewPlatformGroup());
    d3d.setVisible();
  }
}
