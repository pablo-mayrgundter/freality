package vr.cpack.space;

import com.sun.j3d.utils.behaviors.mouse.*;
import gfx.Display3D;
import gfx.FullScreenableFrame;


import javax.media.j3d.*;
import javax.vecmath.Point3d;

import org.freality.gui.three.*;

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
public class SimpleSolarSystem extends Display3D {

  public SimpleSolarSystem() {
    super(new Scene());

    final SpaceShipNavigator nav =
      new SpaceShipNavigator(vpg.getTransformGroup(), view, scene.getBounds());
 
    final FlyToBehavior ftb =
      new FlyToBehavior(vpg.getTransformGroup(), ((Scene)scene).destinationNodes, scene.getBounds());

    scene.addChild(ftb);
  }

  public static void main(final String [] args) {
    final SimpleSolarSystem ss = new SimpleSolarSystem();
    ss.setVisible();
  }
}
