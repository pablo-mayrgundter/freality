package net.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class Load {

  static long start,
    connect, minConnect = Long.MAX_VALUE, maxConnect = Long.MIN_VALUE, totConnect,
    stream, minStream = Long.MAX_VALUE, maxStream = Long.MIN_VALUE, totStream,
    elapsed, minElapsed = Long.MAX_VALUE, maxElapsed = Long.MIN_VALUE, totElapsed;
  static final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  static final byte [] inBuf = new byte[1024];

  static byte [] get(InetAddress addr, String host, int port, String path) throws IOException {
    Socket s = new Socket(addr, port);
    connect = System.currentTimeMillis() - start;
    final InputStream is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    String req =
      "GET " + path + " HTTP/1.1\r\n" +
      "Host: " + host + ":" + port + "\r\n" +
      "Accept: */*\r\n" +
      "Connection: close\r\n\r\n";
    if (trace) {
      System.err.print(req);
    }
    byte [] buf = req.getBytes();
    os.write(buf, 0, buf.length);
    os.flush();

    baos.reset();
    int len;
    try {
      while ((len = is.read(inBuf)) != -1) {
        if (baos.size() == 0) {
          stream = System.currentTimeMillis() - start;
        }
        baos.write(inBuf, 0, len);
      }
    } catch (IOException e) {
      if (trace) {
      System.err.println(e);
      }
      return new byte[]{};
    }
    s.close();
    return baos.toByteArray();
  }

  static final String HOST = System.getProperty("host", "localhost");
  static final int PORT = Integer.parseInt(System.getProperty("port", "80"));
  static final int NUM_FETCHES = Integer.parseInt(System.getProperty("fetches", "10"));
  static final String PATH = System.getProperty("path", "/");
  static final boolean trace = Boolean.getBoolean("trace");

  public static void main(String [] args) throws IOException {
    long bytes = 0;
    InetAddress hostAddr = InetAddress.getByName(HOST);
    for (int i = 0; i < NUM_FETCHES; i++) {
      start = System.currentTimeMillis();
      byte [] buf = get(hostAddr, HOST, PORT, PATH);
      elapsed = System.currentTimeMillis() - start;
      bytes += buf.length;
      if (trace) {
        System.err.println(new String(buf));
      }

      minConnect = Math.min(minConnect, connect);
      maxConnect = Math.max(maxConnect, connect);
      totConnect += connect;

      minStream = Math.min(minStream, stream);
      maxStream = Math.max(maxStream, stream);
      totStream += stream;

      minElapsed = Math.min(minElapsed, elapsed);
      maxElapsed = Math.max(maxElapsed, elapsed);
      totElapsed += elapsed;
    }

    double avgBytes = (double) bytes / (double) elapsed;
    double secs = (double) elapsed / 1000.0;
    System.out.printf("%d fetches, %d bytes, in %.5f seconds\n", NUM_FETCHES, bytes, secs);
    System.out.printf("%.5f mean bytes/connection\n", avgBytes);
    System.out.printf("%.5f fetches/sec, %.5f bytes/sec\n", (double) NUM_FETCHES / secs, bytes / secs);
    System.out.printf("msecs/connect: %.5f mean, %.5f max, %.5f min\n",
                      (double) totConnect / (double) NUM_FETCHES, (double) maxConnect, (double) minConnect);
    System.out.printf("msecs/stream: %.5f mean, %.5f max, %.5f min\n",
                      (double) totStream / (double) NUM_FETCHES, (double) maxStream, (double) minStream);
    System.out.printf("msecs/elapsed: %.5f mean, %.5f max, %.5f min\n",
                      (double) totElapsed / (double) NUM_FETCHES, (double) maxElapsed, (double) minElapsed);
  }
}