package vr.cpack.space;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.*;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Vector3d;

//import org.freality.gui.three.MouseLook;
//import org.freality.gui.three.ExponentialKeyNavigatorBehavior;

class SpaceShipNavigator {

    final View view;
    final Bounds sceneBounds;

    /**
     * Makes nav behavior for the given TransformGroup above the
     * ViewPlatform.  Manipulating this TG is the basis for navigation
     * through the scene.
     */
    public SpaceShipNavigator(TransformGroup tg, View view, Bounds sceneBounds) {
        this(tg, view, sceneBounds, makeDefaultTransform(), 10000f);
    }

    public SpaceShipNavigator(TransformGroup tg, View view, Bounds sceneBounds, Transform3D t3d, float activationRadius) {
        this.view = view;
        this.sceneBounds = sceneBounds;
        tg.setTransform(t3d);

        // Setup view.
        makeViewBehaviors(tg, sceneBounds, view.getCanvas3D(0));
    }

    static Transform3D makeDefaultTransform() {
        final Transform3D defaultViewT3D = new Transform3D();
        defaultViewT3D.setTranslation(new Vector3d(0.0, 0.0, 30f));
        //    double viewDistance = 1.0 / Math.tan((Math.PI / 4.0) / 2.0);
        //    defaultViewT3D.set(new Vector3d(0.0, 0.0, viewDistance));
        //    defaultViewT3D.lookAt(new Point3d(0, 0, 10), new Point3d(0,0,0), new Vector3d(0, 1, 0));
        return defaultViewT3D;
    }

    void makeMouseBehaviors(TransformGroup tg, Bounds sceneBounds) {
        final MouseRotate rotBehavior = new MouseRotate();
        rotBehavior.setTransformGroup(tg);
        rotBehavior.setSchedulingBounds(sceneBounds);
        tg.addChild(rotBehavior);
        /*
        final MouseZoom zoomBehavior = new MouseZoom();
        zoomBehavior.setTransformGroup(tg);
        zoomBehavior.setSchedulingBounds(sceneBounds);
        tg.addChild(zoomBehavior);
        */
    }

    void makeViewBehaviors(TransformGroup vpGroup, Bounds sceneBounds, Canvas3D canvas) {
        //        final MouseLook lookBehavior = new MouseLook(canvas, MouseLook.INVERT_INPUT);
        //        lookBehavior.setTransformGroup(vpGroup);
        //        lookBehavior.setSchedulingBounds(sceneBounds);
        //        vpGroup.addChild(lookBehavior);

        //        final KeyNavigatorBehavior keyBehavior = new KeyNavigatorBehavior(vpGroup);
        //        final ExponentialKeyNavigatorBehavior keyBehavior = new ExponentialKeyNavigatorBehavior(vpGroup, 1000000000000.0); // for stars
        //        final ExponentialKeyNavigatorBehavior keyBehavior = new ExponentialKeyNavigatorBehavior(vpGroup, 100.0);
        //        final ExponentialKeyNavigatorBehavior keyBehavior = new ExponentialKeyNavigatorBehavior(vpGroup, 100000.0);
        //        keyBehavior.setSchedulingBounds(sceneBounds);
        //        vpGroup.addChild(keyBehavior);
    }
}
