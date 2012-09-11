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

    final OutputStream os;
    final BufferedReader r;
    final byte [] buf;

    Handler(Socket s) throws IOException {
      assert debug("Handling connection: " + s);
      buf = new byte[1024];
      r = new BufferedReader(new InputStreamReader(s.getInputStream()));
      try {
        os = s.getOutputStream();
      } catch(IOException e) {
        r.close();
        throw e;
      }
    }

    public void run() {
      try {
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
          if (parts.length < 3) {
            responseHeader(400);
            return;
          }
          sendFile(parts[1]);
        }
      } catch (IOException e) {
        e.printStackTrace();
        logger.warning("Connection service failed: " + e);
      } finally {
        try {
          try {
            r.close();
          } finally {
            os.close();
          }
        } catch(IOException e) {
          logger.warning("Connection close failed: " + e);
        }
      }
    }

    void responseHeader(int code) throws IOException {
      String msg = "HTTP/1.0 " + code + " ";
      switch(code) {
        case 200: msg += "OK"; break;
        case 400: msg += "Client error"; break;
        case 404: msg += "File not found"; break;
      }
      msg = msg += "\r\n\r\n";
      os.write(msg.getBytes());
    }

    void sendFile(String filename) throws IOException {
      filename = translateFilename(filename);
      assert debug("Serving file: " + filename);
      FileInputStream fr = null;
      try {
        fr = new FileInputStream(filename);
        responseHeader(200);
        int len;
        while ((len = fr.read(buf)) != -1) {
          os.write(buf, 0, len);
        }
      } catch(FileNotFoundException e) {
        responseHeader(404);
      } finally {
        if (fr != null)
          fr.close();
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