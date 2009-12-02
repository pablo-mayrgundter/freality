package vr.cpack.space.sceneobject;

import com.sun.j3d.utils.geometry.Sphere;
import vr.cpack.space.SceneScaling;
import vr.cpack.space.model.Planet;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import org.freality.gui.three.Colors;
import org.freality.gui.three.Textures;

public class Planet3D extends Group {

    static final int DEFAULT_RESOLUTION = 120;

    final Planet mCelestialBody;
    final SceneScaling mScaling;

    public Planet3D(Planet planet, SceneScaling scaling, String textureURLBase) {
        mCelestialBody = planet;
        mScaling = scaling;

        URL url = null;
        try {
            url = new URL(textureURLBase + "/" + mCelestialBody.name + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (url == null)
            addChild(makeSphere(makeAppearance()));
        else
            addChild(makeSphere(makeTexturedAppearance(url)));

        if (planet.name.equalsIgnoreCase("earth")) {
            try {
                url = new URL(textureURLBase + "/" + mCelestialBody.name + "-atmos.jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (url != null)
                addChild(makeSphere(1.001f * (float) mScaling.scale(mCelestialBody.meanRadius).scalar,
                                    makeTransparentTexturedAppearance(url)));
        }
    }

    Sphere makeSphere(Appearance app) {
        return makeSphere((float) mScaling.scale(mCelestialBody.meanRadius).scalar, app);
    }

    Sphere makeSphere(float radius, Appearance app) {
        return new Sphere(radius,
                          Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS,
                          DEFAULT_RESOLUTION,
                          app);
    }

    Appearance makeAppearance() {
        final Appearance starApp = new Appearance();
        final Material m = new Material(Colors.BLACK, Colors.BLACK, makeColor(), Colors.BLACK, 88f);
        m.setLightingEnable(true);
        starApp.setMaterial(m);
        return starApp;
    }

    Appearance makeTexturedAppearance(URL textureURL) {
        final Appearance app = new Appearance();
        final Material m = new Material(Colors.BLACK, Colors.BLACK, makeColor(), Colors.BLACK, 88f);
        m.setLightingEnable(true);
        app.setMaterial(m);
        Textures.addTexture(app, textureURL, TextureAttributes.MODULATE);
        return app;
    }

    Appearance makeTransparentTexturedAppearance(URL textureURL) {
        final Appearance app = new Appearance();
        final Material m = new Material(Colors.BLACK, Colors.BLACK, makeColor(), Colors.BLACK, 88f);
        m.setLightingEnable(true);
        app.setMaterial(m);
        Textures.addAlphaTransparentTexture(app, textureURL);
        return app;
    }

    Color3f makeColor() {
        final float albedo = (float) ((Planet) mCelestialBody).albedo;
        return new Color3f(albedo, albedo, albedo);
    }
}
