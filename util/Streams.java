package util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * Common stream utilities, e.g. byte [] readFully(InputStream).
 *
 * @author Pablo Mayrgundter
 */
public class Streams {

  public static interface LineFn {
    public void apply(String line);
  }

  public static final void readLines(InputStream is, LineFn fn) throws IOException {
    final LineNumberReader lnr = new LineNumberReader(new BufferedReader(new InputStreamReader(is)));
    String line;
    while ((line = lnr.readLine()) != null) {
      try {
        fn.apply(line);
      } catch (IllegalArgumentException e) {
        System.err.printf("Error on line (%d): %s\n", lnr.getLineNumber(), e);
      }
    }
  }

  public static final byte [] readFully(File f) throws IOException {
    final FileInputStream fis = new FileInputStream(f);
    try {
      return readFully(fis);
    } finally {
      fis.close();
    }
  }

  public static final byte [] readFully(InputStream is) throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte [] buf = new byte[4096];
    int len;
    while ((len = is.read(buf)) != -1)
      baos.write(buf, 0, len);
    return baos.toByteArray();
  }
}
