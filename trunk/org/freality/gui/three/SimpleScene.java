package org.freality.gui.three;

import com.sun.image.codec.jpeg.JPEGCodec;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import fmc.cpack.space.sceneobject.Rings3D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Switch;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransformInterpolator;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;


/**
 * This class sets up a basic J3D scene to which shapes, lights,
 * etc. can be added.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class SimpleScene {

    protected Bounds sceneBounds;
    BranchGroup bg = null;

    public SimpleScene() {
        sceneBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10000.0);
    }

    public Bounds getSceneBounds() {
        return sceneBounds;
    }


    public BranchGroup getSceneGraph() {
        if (bg == null)
            bg = createSceneGraph();
        return bg;
    }

    /**
     * Subclasses override this to create a scene.
     */
    public BranchGroup createSceneGraph() {
        final BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        // Add sceneTg
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setBounds(sceneBounds);

        tg = rings(tg);
        tg = earth(tg);

        final PointLight light = new PointLight(true, Colors.WHITE, new Point3f(0f, 0f, 0f), new Point3f(1f, 0f, 0f));
        light.setInfluencingBounds(sceneBounds);
        light.setPosition(20f, 20f, 20f);
        bg.addChild(light);

        final Light ambLight = new AmbientLight(true, Colors.WHITE);
        tg.addChild(ambLight);

        final Transform3D zAxis = new Transform3D();
        zAxis.lookAt(new Point3d(0,0,0), new Point3d(1,0,0), new Vector3d(0,1,0));
        //        interp = new PositionInterpolator(alpha, tg, zAxis, 0.0f, 10000f);
        //        interp.setSchedulingBounds(schedBounds);
        //        tg.addChild(interp);

        // Add behaviours.
        makeSceneMouseBehaviors(tg);

        bg.addChild(tg);
        bg.compile();

        System.err.println("Created SceneBranchGroup.");
        return bg;
    }

    TransformGroup rings(TransformGroup tg) {
        if (true) return tg;
        tg.addChild(new Rings3D(10f, 20f));
        return tg;
    }

    TransformGroup earth(TransformGroup tg) {
        float sceneRadius = 10f;
        org.freality.io.loader.java.Handler.register();
        java.net.URL [] skinURLs = new URL[2];
        try {
            skinURLs[0] = new java.net.URL("java:fmc/cpack/space/textures/earth.jpg");
            skinURLs[1] = new java.net.URL("java:fmc/cpack/space/textures/earth-atmos.jpg");
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        final Appearance earthApp = new Appearance();
        Textures.addTexture(earthApp, skinURLs[0], TextureAttributes.MODULATE);
        tg.addChild(makeTexturedSphere(sceneRadius, earthApp));

        final Appearance atmosApp = new Appearance();
        Textures.addAlphaTransparentTexture(atmosApp, skinURLs[1]);
        tg.addChild(makeTexturedSphere(sceneRadius, atmosApp));

        final Transform3D yAxis = new Transform3D();

        Alpha alpha = new Alpha();
        alpha.setIncreasingAlphaDuration(60000);
        final BoundingSphere schedBounds =
            new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        TransformInterpolator interp;

        tg = wrap(tg);

        interp = new RotationInterpolator(alpha, tg, yAxis, 0.0f, (float) Math.PI * 2.0f);
        interp.setSchedulingBounds(schedBounds);
        tg.addChild(interp);

        return wrap(tg);
    }

    Appearance makeAppearance(Texture texture) {
        final Appearance appearance = makeAppearance();
        final TextureAttributes ta = new TextureAttributes();
        appearance.setTexture(texture);
        ta.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(ta);
        return appearance;
    }

    protected Appearance makeAppearance() {
        final Appearance appearance = new Appearance();
        final Material m = new Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.GREY2, 0.12f * 128.0f); // Earth's albedo.
        m.setLightingEnable(true);
        appearance.setMaterial(m);
        return appearance;
    }

    BufferedImage getImage(URL sourceImage) {
        BufferedImage bi = null;
        InputStream is = null;
        try {
            is = sourceImage.openStream();
            bi = JPEGCodec.createJPEGDecoder(is).decodeAsBufferedImage();
        } catch(IOException e) {
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return bi;
    }


    static final int SPHERE_RESOLUTION = 120;

    Sphere makeTexturedSphere(float radius, Appearance appearance) {
        return makeTexturedSphere(radius, SPHERE_RESOLUTION, appearance);
    }

    Sphere makeTexturedSphere(float radius, int resolution, Appearance appearance) {
        return new Sphere(radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, resolution, appearance);
    }

    TransformGroup wrap(TransformGroup tg) {
        final TransformGroup tgWrapper = new TransformGroup();
        tgWrapper.addChild(tg);
        tgWrapper.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tgWrapper.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        return tgWrapper;
    }

    // Not sure which params this needs.
    protected void makeBasicPickingBehaviors(BranchGroup sceneBg, TransformGroup sceneTg, TransformGroup vpTg) {
        //    PickZoomBehavior pickZoomBehavior = new PickZoomBehavior(this, sceneBg, vpTg);
        //    pickZoomBehavior.setSchedulingBounds(sceneBounds);
        //    sceneTg.addChild(pickZoomBehavior);
    }

    protected void makeSceneMouseBehaviors(TransformGroup tg) {
        final MouseRotate rotBehavior = new MouseRotate();
        rotBehavior.setTransformGroup(tg);
        rotBehavior.setSchedulingBounds(sceneBounds);
        tg.addChild(rotBehavior);

        final MouseZoom zoomBehavior = new MouseZoom();
        zoomBehavior.setTransformGroup(tg);
        zoomBehavior.setSchedulingBounds(sceneBounds);
        tg.addChild(zoomBehavior);
    }
}
