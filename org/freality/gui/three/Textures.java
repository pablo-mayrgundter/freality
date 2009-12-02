package org.freality.gui.three;

import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.Appearance;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import java.awt.Component;
import java.awt.Panel;
import java.net.URL;

public class Textures {

    /** Observer used by TextureLoader. */
    static final Component OBSERVER = new Panel();

    public static final Texture loadTexture(URL textureURL) {
        return new TextureLoader(textureURL, TextureLoader.GENERATE_MIPMAP, OBSERVER).getTexture();
    }

    /**
     * Adds the given texture as a to the given appearance.
     * @param texMode A mode defined in TextureAttributes.
     */
    public static final void addTexture(Appearance app, URL textureURL, int texMode) {
        final Texture tex = loadTexture(textureURL);
        if (tex != null) {
            app.setTexture(tex);
            final TextureAttributes ta = new TextureAttributes();
            ta.setTextureMode(texMode);
            app.setTextureAttributes(ta);
            app.setMaterial(new javax.media.j3d.Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.BLACK, 0.88f * 128f));
        }
    }

    /**
     * Adds the given texture as a transparent color-combined texture
     * to the given appearance.  The texture attributes are set such
     * that the alpha channel of the given texture encodes
     * transparency.
     */
    public static final void addAlphaTransparentTexture(Appearance app, URL textureURL) {
        final Texture tex = loadTexture(textureURL);
        if (tex != null) {
            app.setTexture(tex);

            final TextureAttributes ta = new TextureAttributes();
            //            ta.setTextureMode(TextureAttributes.MODULATE);
            ta.setTextureMode(TextureAttributes.COMBINE);
            ta.setCombineRgbSource(0, TextureAttributes.COMBINE_TEXTURE_COLOR);
            app.setTextureAttributes(ta);

            final TransparencyAttributes transparency =
                new TransparencyAttributes(TransparencyAttributes.BLENDED, 1f,
                                           TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA,
                                           TransparencyAttributes.BLEND_ONE);
            app.setTransparencyAttributes(transparency);
            app.setMaterial(new javax.media.j3d.Material(Colors.BLACK, Colors.BLACK, Colors.WHITE, Colors.BLACK, 0.88f * 128f));
        }
    }
}
