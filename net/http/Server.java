package net.http;

import util.Flags;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * A simple multi-threaded HTTP file server.  Handles only GET
 * requests, serving files below the directory in which the server is
 * started.
 *
 * To run:
 *
 *   java net.http.Server
 *
 * or if you can't run on priviledged ports (&lt;1024), pick a high
 * one:
 *
 *   java -Dport=8080 net.http.Server
 *
 * Loadtest using acme.com's http_load:
 *
 *   > cat /proc/cpuinfo
 *   ... Intel(R) Xeon(R) CPU           X5679  @ 3.20GHz ...
 *   ... Intel(R) Xeon(R) CPU           X5679  @ 3.20GHz ...
 *   ... Intel(R) Xeon(R) CPU           X5679  @ 3.20GHz ...
 *   > dd if=/dev/zero of=10k.dat bs=1024 count=10
 *   > echo "http://localhost:8080/10k.dat" > test.url
 *   > ./http_load -p 10 -f 10000 test.url
 *   # Throwaway
 *   > ./http_load -p 10 -f 100000 test.url
 *   100000 fetches, 10 max parallel, 1.024e+09 bytes, in 34.9176 seconds
 *   10240 mean bytes/connection
 *   2863.88 fetches/sec, 2.93262e+07 bytes/sec
 *   msecs/connect: 0.0995282 mean, 2.717 max, 0.037 min
 *   msecs/first-response: 3.18724 mean, 202.23 max, 0.315 min
 *   HTTP response codes:
 *     code 200 -- 100000
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
    DateFormat DF;

    Handler(Socket s) throws IOException {
      assert debug("Handling connection: " + s);
      buf = new byte[1024];
      r = new BufferedReader(new InputStreamReader(s.getInputStream()));
      try {
        os = s.getOutputStream();
      } catch (IOException e) {
        r.close();
        throw e;
      }
      DF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
      DF.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
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
            responseHeaders(400, null);
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

    void responseHeaders(int code, String mime, long ... contentLength) throws IOException {
      String msg = "HTTP/1.0 " + code + " ";
      switch(code) {
        case 200: msg += "OK"; break;
        case 400: msg += "Client error"; break;
        case 404: msg += "File not found"; break;
      }
      msg += "\r\n";
      if (mime != null) {
        msg += "Content-Type: " + mime + "\r\n";
      }
      if (contentLength.length != 0) {
        msg += "Content-Length: " + contentLength[0] + "\r\n";
      }
      msg += "Cache-Control: private, max-age=0\r\n";
      msg += "Expires: -1\r\n";
      msg += "Server: yo\r\n";
      msg += "Date: " + DF.format(new Date()) + "\r\n";
      msg += "\r\n";
      os.write(msg.getBytes());
    }

    String getMime(String filename) {
      final String [] parts = filename.split("\\.");
      String type = "application/octet";
      if (parts.length > 0) {
        final String ftype = parts[parts.length - 1];
        if (ftype.matches("(png|jpg|jpeg|gif)")) {
          type = "image/" + ftype;
        } else if (ftype.matches("(html|xml|txt|css)")) {
          type = "text/" + ftype;
        } else if (ftype.matches("js")) {
          type = "text/javascript";
        }
      }
      return type;
    }

    void sendFile(String filename) throws IOException {
      String mime = getMime(filename);
      filename = translateFilename(filename);
      assert debug("Serving file: " + filename);
      FileInputStream fr = null;
      try {
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        final long fileLen = raf.length();
        responseHeaders(200, mime, fileLen);
        // Send file.
        FileChannel fc = raf.getChannel();
        WritableByteChannel out = java.nio.channels.Channels.newChannel(os);
        long sentLen = 0;
        while (sentLen < fileLen) {
          sentLen += fc.transferTo(sentLen, fileLen - sentLen, out);
        }
      } catch (FileNotFoundException e) {
        responseHeaders(404, null);
      } finally {
        if (fr != null) {
          fr.close();
        }
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
