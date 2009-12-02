package org.freality.io.loader.java;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;

/**
 * Allows reading of java system resources within the standard URL
 * framework.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @author <a href="mailto:pablo@reeltwo.com">Pablo Mayrgundter</a>
 */
public class Handler extends URLStreamHandler {

  private static final String PROPERTY = "java.protocol.handler.pkgs";
  private static final String PACKAGE = "org.freality.io.loader";

  /**
   * Registers this handler with the JVM by adding the package path
   * to the system property: java.protocol.handler.pkgs
   *
   */
  public static void register() {
    Properties p = System.getProperties();
    String s = (String)p.get(PROPERTY);
    if ((s == null) || (s.indexOf(PACKAGE) == -1)) {
      s = PACKAGE;
    } else {
      s = s + "|" + PACKAGE;
    }
    p.put(PROPERTY, s);
  }

  protected URLConnection openConnection(URL u) {
    return new JavaURLConnection(u);
  }
}
