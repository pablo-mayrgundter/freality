package vr.cpack.space.sceneobject;

import com.sun.j3d.utils.geometry.Sphere;

import vr.cpack.space.SceneScaling;
import vr.cpack.space.model.CelestialBody;
import vr.cpack.space.model.Star;

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

  public Star3D(Star star, SceneScaling scaling, String textureURLBase) {
    mCelestialBody = star;
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
      addChild(makeSphere(makeTexturedAppearance(url, TextureAttributes.DECAL)));
  }

  Sphere makeSphere(Appearance app) {
    return new Sphere((float) mScaling.scale(mCelestialBody.meanRadius).scalar,
                      Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS,
                      DEFAULT_RESOLUTION,
                      app);
  }

  Appearance makeAppearance() {
    final Appearance starApp = new Appearance();
    final Material m = new Material(Colors.BLACK, Colors.BLACK, Colors.BLACK, Colors.WHITE, 0f);
    m.setLightingEnable(false);
    starApp.setMaterial(m);
    return starApp;
  }

  Appearance makeTexturedAppearance(URL textureURL, int texMode) {
    final Appearance starApp = new Appearance();
    final Material m = new Material(Colors.BLACK, Colors.BLACK, Colors.BLACK, Colors.WHITE, 0f);
    m.setLightingEnable(false);
    starApp.setMaterial(m);
    Textures.addTexture(starApp, textureURL, texMode);
    return starApp;
  }
}
