package bin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import os.util.HexCharset;

/**
 * Simple HexDump implementation using Java Integer.toHexString()
 * formatting.
 *
 * @author Pablo Mayrgundter
 */
public class HexDump {

  // TODO(pablo): hoist byte i/o to a super-class.
  final ReadableByteChannel in;
  final WritableByteChannel out;
  final ByteBuffer inBuf;
  final ByteBuffer outBuf;

  final CharBuffer charBuf;
  final CharsetDecoder hexDecoder;
  final CharsetDecoder asciiDecoder;

  HexDump(Object input, Object output) throws IOException {
    if (input instanceof File) {
      final RandomAccessFile raf = new RandomAccessFile((File) input, "r");
      in = raf.getChannel();
    } else if (input instanceof InputStream) {
      in = Channels.newChannel((InputStream) input);
    } else {
      throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
    }

    if (output instanceof File) {
      final RandomAccessFile raf = new RandomAccessFile((File) output, "rw");
      out = raf.getChannel();
    } else if (output instanceof OutputStream) {
      out = Channels.newChannel((OutputStream) output);
    } else {
      throw new IllegalArgumentException("Unsupported output type: " + output.getClass().getName());
    }

    hexDecoder = new HexCharset().newDecoder();
    asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    inBuf = ByteBuffer.allocateDirect(1024);
    outBuf = ByteBuffer.allocateDirect(1024);
    charBuf = outBuf.asCharBuffer();
  }

  public void run() throws IOException {
    int len;
    while ((len = in.read(inBuf)) != -1) {
      inBuf.flip();
      hexDecoder.decode(inBuf, charBuf, false);

      charBuf.put('|');

      inBuf.flip();
      asciiDecoder.decode(inBuf, charBuf, false);

      charBuf.put('\n');

      outBuf.flip();
      out.write(outBuf);
      inBuf.clear();
      outBuf.clear();
    }
  }

  public static void main(String [] args) throws IOException {
    HexDump hd = null;
    switch (args.length) {
    case 0: hd = new HexDump(System.in, System.out); break;
    case 1: hd = new HexDump(new File(args[0]), System.out); break;
    default: {
      System.err.printf("Usage: java %s [file]\n", HexDump.class.getName());
      System.exit(1);
    }
    }
    hd.run();
  }
}
