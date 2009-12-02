package vr.cpack.space;

import com.sun.j3d.utils.behaviors.mouse.*;
import gfx.FullScreenableFrame;

import java.awt.Point;
import java.awt.Toolkit;

import javax.media.j3d.*;
import javax.vecmath.Point3d;

import org.freality.gui.three.*;

/**
 * This class sets up a simple solar system model.  The Locale for
 * this scene is the center of our (the Milky Way) galaxy, as there is
 * no higher-level "center" to use as a reference.  Other galaxies and
 * Universe objects can be described in relative position to the
 * center of our galaxy without loss of generality.
 *
 * @author Pablo Mayrgundter
 * @version $Revision: 1.1.1.1 $
 */
public class SimpleSolarSystem extends BranchGroup {

  /** VirtualUniverse -> Locale -> BranchGroups -> TransformGroup -> ViewPlatform <- View */

  final Scene scene;

  /** Create a standalone Solar System scene. */
  public SimpleSolarSystem() {
    this(new VirtualUniverse());
  }

  /** Add this Solar System scene to an existing VirtualUniverse. */
  public SimpleSolarSystem(VirtualUniverse universe) {
    setBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    scene = new Scene(getBounds());
    scene.load();
    addChild(scene);
  }

  public static void main(String [] args) throws Exception {

    final VirtualUniverse universe = new VirtualUniverse();
    final SimpleSolarSystem solarSystemGroup = new SimpleSolarSystem(universe);

    final DefaultView view = new DefaultView();
    final ViewPlatformGroup vpg =
      new ViewPlatformGroup(view, new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                                                     1000000000.0));
    ViewPlatform vp = null;
    if (false) {
      // Doesn't work right.. modifies planet tgs.
      vp = (ViewPlatform) solarSystemGroup.scene.viewPlatforms.get("Earth");
      vpg.getTransformGroup().addChild(vp);
    } else {
      vp = vpg.getViewPlatform();
    }

    view.attachViewPlatform(vp); // Starts viewing.
    solarSystemGroup.addChild(vpg);

    final SpaceShipNavigator nav
      = new SpaceShipNavigator(vpg.getTransformGroup(), view,
                               solarSystemGroup.scene.getBounds());
 
    final FlyToBehavior ftb =
      new FlyToBehavior(vpg.getTransformGroup(), solarSystemGroup.scene.destinationNodes,
                        solarSystemGroup.scene.getBounds());
    solarSystemGroup.addChild(ftb);

    final Locale locale = new Locale(universe);
    solarSystemGroup.compile();
    locale.addBranchGraph(solarSystemGroup); // Make the view group live.

    Canvas3D canvas = view.getCanvas3D();
    final FullScreenableFrame frame = new FullScreenableFrame();
    frame.getContentPane().add(canvas);

    final Toolkit tk = Toolkit.getDefaultToolkit();
    frame.getContentPane().setCursor(tk.createCustomCursor(tk.createImage(new byte[]{}),
                                                           new Point(0, 0), "Pointer"));
    frame.setVisible(true);
  }
}
