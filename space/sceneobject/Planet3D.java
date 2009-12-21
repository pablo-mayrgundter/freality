package space.sceneobject;

import com.sun.j3d.utils.geometry.Sphere;
import space.SceneScaling;
import space.model.CelestialBody;
import space.model.Planet;
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

public class Planet3D extends Star3D {

  public Planet3D(final CelestialBody body, final SceneScaling scaling, final String textureURLBase) {
    super(body, scaling, textureURLBase,
          Colors.WHITE, Colors.GREY2, TextureAttributes.MODULATE,
          (float)((Planet)body).albedo, true);
    if (body.name.equalsIgnoreCase("earth")) {
      final URL atmosTexURL = strToURL(textureURLBase + "/" + mCelestialBody.name + "-atmos.jpg");
      if (atmosTexURL != null)
        addChild(makeSphere(1.001f * getSceneRadius(),
                            makeTransparentTexturedAppearance(atmosTexURL)));
    }
  }

  Appearance makeTransparentTexturedAppearance(final URL textureURL) {
    final Appearance app = new Appearance();
    final float albedo = (float) ((Planet) mCelestialBody).albedo;
    final Color3f atmosColor = new Color3f(albedo, albedo, albedo);
    final Material m = new Material(Colors.BLACK, Colors.BLACK, atmosColor, Colors.BLACK, shinniness);
    m.setLightingEnable(lightingEnabled);
    app.setMaterial(m);
    Textures.addAlphaTransparentTexture(app, textureURL);
    return app;
  }

  Color3f makeColor() {
    final float albedo = (float) ((Planet) mCelestialBody).albedo;
    return new Color3f(albedo, albedo, albedo);
  }
}
