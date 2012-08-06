package net.http;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import util.Flags;

/**
 * A simple multi-threaded HTTP file server.  Handles only GET
 * requests, serving files below the directory in which the server is
 * started.
 *
 * To run:
 *
 *   java -Dport=8080 net.http.Server
 *
 * @author Pablo Mayrgundter
 */
public class Server {

  static final Logger logger = Logger.getLogger(Server.class.getName());

  static final Flags flags = new Flags(Server.class);
  static final int PORT = flags.get("port", "port", 80);

  static final class Handler implements Runnable {

    Socket socket;

    Handler(Socket s) {
      assert debug("Handling connection: " + s);
      socket = s;
    }

    public void run() {
      BufferedReader r = null;
      try {
        r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line, hdr = "";
        while ((line = r.readLine()) != null) {
          if (line.trim().equals("")) {
            break;
          }
          hdr += line + "\n";
        }
        assert debug("Got header: " + hdr);
        if (hdr.startsWith("GET")) {
          String [] parts = hdr.split(" ");
          final String filename = parts[1];
          sendFile(socket, filename);
        }
        r.close();
      } catch (IOException e) {
        logger.warning(""+ e);
      } finally {
        if (r != null) {
          try {
            r.close();
          } catch (IOException e) {}
        }
      }
    }

    void sendFile(Socket s, String filename) throws IOException {
      filename = translateFilename(filename);
      assert debug("Serving file: " + filename);
      byte [] buf = new byte[1024];
      FileInputStream fr = null;
      OutputStream os = null;
      try {
        fr = new FileInputStream(filename);
        os = socket.getOutputStream();
        int len;
        while ((len = fr.read(buf)) != -1) {
          os.write(buf, 0, len);
        }
      } finally {
        if (fr != null)
          fr.close();
        if (os != null)
          os.close();
      }
    }

    String translateFilename(String filename) {
      if (filename.startsWith("/")) {
        filename = filename.substring(1);
      }
      while (filename.startsWith("../")) {
        filename = filename.substring(3);
      }
      return filename;
    }
  }

  public void run() throws IOException {
    // Bind
    ServerSocket ss = new ServerSocket(PORT);
    Socket socket;
    while ((socket = ss.accept()) != null) {
      assert debug("Spawning worker thread: " + socket);
      new Thread(new Handler(socket)).start();
    }
  }

  public static void main(String [] args) throws IOException {
    new Server().run();
  }

  static final boolean debug(String s) {
    logger.info(s);
    return true;
  }
}