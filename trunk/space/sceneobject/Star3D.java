package space.sceneobject;

import com.sun.j3d.utils.geometry.Sphere;

import space.SceneScaling;
import space.model.CelestialBody;
import space.model.Star;

import java.net.URL;

import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;

import org.freality.gui.three.Colors;
import org.freality.gui.three.Textures;

/**
 * @author Pablo Mayrgundter
 */
public class Star3D extends Group {

  static final int DEFAULT_RESOLUTION = 120;

  final Star mCelestialBody;
  final SceneScaling mScaling;

  /**
   * Use lighting to light this object.  Default is false for stars,
   * since they aren't lit by other light but instead emit light
   * themselves.
   */
  final boolean lightingEnabled;
  final float shinniness;
  /** DECAL for star, MODULATE for planet and MODULATE for atmosphere. */
  final int baseTexType;
  final Color3f diffuseColor;
  final Color3f specularColor;

  public Star3D(final CelestialBody star, final SceneScaling scaling, final String textureURLBase) {
    this(star, scaling, textureURLBase,
         Colors.BLACK, Colors.WHITE, TextureAttributes.DECAL, 0, false);
  }

  public Star3D(final CelestialBody star, final SceneScaling scaling, final String textureURLBase,
                final Color3f diffuseColor, final Color3f specularColor, final int baseTexType,
                final float shinniness, final boolean lightingEnabled) {
    mCelestialBody = (Star)star;
    mScaling = scaling;
    this.diffuseColor = diffuseColor;
    this.specularColor = specularColor;
    this.baseTexType = baseTexType;
    this.shinniness = shinniness;
    this.lightingEnabled = lightingEnabled;

    final URL baseTexURL = strToURL(textureURLBase + "/" + mCelestialBody.name + ".jpg");
    if (baseTexURL == null)
      addChild(makeSphere(makeAppearance(null)));
    else
      addChild(makeSphere(makeAppearance(baseTexURL)));
  }

  float getSceneRadius() {
    return (float) mScaling.scale(mCelestialBody.meanRadius).scalar;
  }

  Sphere makeSphere(final Appearance app) {
    return makeSphere(getSceneRadius(), app);
  }

  Sphere makeSphere(final float radius, final Appearance app) {
    return new Sphere(radius,
                      Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS,
                      DEFAULT_RESOLUTION,
                      app);
  }

  Appearance makeAppearance(final URL textureURL) {
    final Appearance app = new Appearance();
    final Material m = new Material(Colors.BLACK, Colors.BLACK, diffuseColor, specularColor, shinniness);
    m.setLightingEnable(lightingEnabled);
    app.setMaterial(m);
    if (textureURL != null)
      Textures.addTexture(app, textureURL, baseTexType);
    return app;
  }

  static URL strToURL(final String url) {
    try {
      return new URL(url);
    } catch (final java.net.MalformedURLException e) {
      System.err.println("Couldn't load texture at: "+ url + ". "+ e);
      return null;
    }
  }
}
