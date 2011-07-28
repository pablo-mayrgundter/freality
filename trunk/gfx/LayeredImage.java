package gfx;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Container object to manage multiple image layers.
 */
public class LayeredImage {

  final BufferedImage [] layers;
  final int width, height;

  public LayeredImage(final int numLayers, final int width, final int height) {
    layers = new BufferedImage[numLayers];
    this.width = width;
    this.height = height;
    for (int i = 0; i < numLayers; i++) {
      layers[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
  }

  public void draw(final Graphics g) {
    for (int i = layers.length - 1; i >= 0; i--) {
      g.drawImage(layers[i], 0, 0, null);
    }
  }

  public Graphics getGraphics(final int level) {
    return layers[level].getGraphics();
  }
}