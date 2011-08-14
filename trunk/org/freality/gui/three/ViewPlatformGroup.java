package org.freality.gui.three;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple class for holding the ViewPlatform.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class ViewPlatformGroup extends BranchGroup {

  final View view;
  final ViewPlatform vp;

  /**
   * The TransformGroup above the ViewPlatform.  Manipulating this
   * TG is the basis for navigation through the scene.
   */
  final TransformGroup tg;

  public ViewPlatformGroup(View view, Bounds sceneBounds) {
    this(view, sceneBounds, makeDefaultTransform(), 10000f);
  }

  public ViewPlatformGroup(View view, Bounds sceneBounds, Transform3D t3d, float activationRadius) {
    this.view = view;
    setBoundsAutoCompute(false);
    setBounds(sceneBounds);
    tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    tg.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.setTransform(t3d);

    // Setup view.
    vp = new ViewPlatform();
    vp.setCapability(Node.ALLOW_BOUNDS_WRITE);
    vp.setActivationRadius(activationRadius);
    tg.addChild(vp);

    makeViewBehaviors();
    addChild(tg);
  }

  public ViewPlatform getViewPlatform() {
    return vp;
  }

  public TransformGroup getTransformGroup() {
    return tg;
  }

  static Transform3D makeDefaultTransform() {
    Transform3D defaultViewT3D = new Transform3D();
    defaultViewT3D.setTranslation(new Vector3d(0.0, 0.0, 30f));
    //    double viewDistance = 1.0 / Math.tan((Math.PI / 4.0) / 2.0);
    //    defaultViewT3D.set(new Vector3d(0.0, 0.0, viewDistance));
    //    defaultViewT3D.lookAt(new Point3d(0, 0, 10), new Point3d(0,0,0), new Vector3d(0, 1, 0));
    return defaultViewT3D;
  }

  /**
   * Sets up mouse and keyboard behaviors that control movement of the given TransformGroup.
   */
  void makeViewBehaviors() {
    // Create the translate behavior node
    // getCanvas3D() is why this method is in this class.
    /*
      MouseLook lookBehavior = new MouseLook(view.getCanvas3D(), MouseLook.INVERT_INPUT);
      lookBehavior.setTransformGroup(tg);
      lookBehavior.setSchedulingBounds(sceneBounds);
      tg.addChild(lookBehavior);

      KeyNavigatorBehavior keyBehavior = new KeyNavigatorBehavior(tg);
      keyBehavior.setSchedulingBounds(sceneBounds);
      tg.addChild(keyBehavior);
    */
  }

  public void setViewPlatform(ViewPlatform vp) {
    view.attachViewPlatform(vp);
  }
}
