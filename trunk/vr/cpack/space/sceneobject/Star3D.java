package vr.cpack.space.sceneobject;

import org.freality.gui.three.AppearanceUtil;
import org.freality.gui.three.Colors;
import org.xith3d.behaviors.TransformationDirectives;
import org.xith3d.behaviors.impl.RotatableGroup;
import org.xith3d.render.loop.scheduler.Animator;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

    /*
    tilt
      |
     rot
      |
    Shape
    */
public class Star3D extends Group {

    final Group mHeadTG;

    public Star3D (final String name, final float radius,
                   final float tiltDegrees, final float rotPeriod,
                   final Animator animator) {
        mHeadTG = new TransformGroup();
        mHeadTG.addChild(createTexturedSphere(name, radius));
        addChild(rotateOnTiltedAxis(mHeadTG, tiltDegrees, rotPeriod, animator));
    }

    protected Shape3D createTexturedSphere (final String textureBasename, final float radius) {
        final Shape3D shape = AppearanceUtil.makeTexturedSphere(radius, textureBasename + ".jpg");
        shape.getAppearance().getMaterial().setEmissiveColor(Colors.WHITE);
        return shape;
    }

    TransformGroup rotateOnTiltedAxis (final Node node,
                                       final float tiltDegrees,
                                       final float rotPeriod,
                                       final Animator animator) {

        final RotatableGroup rotGroup = createRotatableGroup(rotPeriod, animator);
        rotGroup.addChild(node);

        final TransformGroup tg = wrap(rotGroup);
        final Transform3D tilt = new Transform3D();
        tilt.rotZ((float) Math.toRadians(tiltDegrees));
        tg.setTransform(tilt);

        return tg;
    }

    RotatableGroup createRotatableGroup (final float rotPeriod, final Animator animator) {
        final RotatableGroup rotGroup = new RotatableGroup(new TransformationDirectives(0, rotPeriod, 0));
        animator.addAnimatableObject(rotGroup);
        return rotGroup;
    }

    protected TransformGroup wrap (final TransformGroup tg) {
        final TransformGroup wrapper = new TransformGroup();
        wrapper.addChild(tg);
        return wrapper;
    }
}
