package util;

import java.io.*;

public class Streams {
  public static byte [] readFully(InputStream is) throws IOException {
    byte [] buf = new byte[1024];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int len;
    while ((len = is.read(buf)) != -1) {
      baos.write(buf, 0, len);
    }
    return baos.toByteArray();
  }
}