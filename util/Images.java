package util;

import com.sun.image.codec.jpeg.JPEGCodec;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;

public final class Images {
  public static BufferedImage getImage(final URL sourceImage) {
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
}