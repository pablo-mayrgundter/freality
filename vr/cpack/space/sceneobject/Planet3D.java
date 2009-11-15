package vr.cpack.space.sceneobject;

import javax.vecmath.Color3f;
import org.freality.gui.three.AppearanceUtil;
import org.freality.gui.three.SceneScaling;
import org.xith3d.render.loop.scheduler.Animator;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransparencyAttributes;

public final class Planet3D extends Star3D {

    public Planet3D (final String name, final float radius,
                     final float tiltDegrees, final float rotPeriod,
                     final Animator animator,
                     final boolean hasAtmosphere) {
        super (name, radius, tiltDegrees, rotPeriod, animator);
        if (hasAtmosphere)
            mHeadTG.addChild(createAtmosphere(name, radius));
    }

    protected Shape3D createTexturedSphere (final String textureBasename, final float radius) {
        final Shape3D shape = AppearanceUtil.makeTexturedSphere(radius, textureBasename + ".jpg");
        //        shape.getAppearance().getMaterial().setEmissiveColor(Colors.WHITE);
        return shape;
    }

    protected Shape3D createAtmosphere (final String name, final float radius) {
        final Shape3D atmos = AppearanceUtil.makeTexturedSphere(radius * 1.001f, name + "-atmos.jpg");
        final TransparencyAttributes atmosTransparency =
            new TransparencyAttributes(TransparencyAttributes.BLENDED, 1,
                                       TransparencyAttributes.BLEND_SRC_ALPHA,
                                       TransparencyAttributes.BLEND_ONE);
        atmos.getAppearance().setTransparencyAttributes(atmosTransparency);
        return atmos;
    }
}
