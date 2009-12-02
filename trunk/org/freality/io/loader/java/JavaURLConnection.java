package org.freality.io.loader.java;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Allows reading of java system resources within the standard URL
 * framework.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @author <a href="mailto:pablo@reeltwo.com">Pablo Mayrgundter</a>
 */
public class JavaURLConnection extends URLConnection {

  public JavaURLConnection(URL url) {
    super(url);
  }

  public void connect() {
  }

  public InputStream getInputStream() throws IOException {
    URL url = getURL();
    String check = url.getProtocol().toLowerCase();
    if (!check.equals("java")) {
      throw new IOException("Incorrect protocol field: "
                            + check);
    }
    String resource = url.getFile();
    if (resource.startsWith("/")) {
      resource = resource.substring(1);
    }
    //System.err.println("RESOURCE: " + resource);
    InputStream input = ClassLoader.getSystemResourceAsStream(resource);
    if (input == null) {
      throw new IOException("No system resource with name: "
                            + url
                            +" was found.");
    }
    return input;
  }
}

