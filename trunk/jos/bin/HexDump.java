package jos.bin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import jos.util.HexCharset;

/**
 * Simple HexDump implementation using Java Integer.toHexString()
 * formatting.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class HexDump extends UNIXProgram implements Runnable {

    HexDump(final Object inputType, final Object outputType) throws IOException {
        super(inputType, outputType);
    }

    public void run() {
        int lineCount = 0;
        String line;
        final CharsetDecoder hexDecoder = new HexCharset().newDecoder();
        final CharsetDecoder asciiDecoder = Charset.forName("US-ASCII").newDecoder();
        final ByteBuffer buf = ByteBuffer.allocateDirect(128);
        final ByteBuffer writeBuf = ByteBuffer.allocateDirect(512);
        final CharBuffer charBuf = writeBuf.asCharBuffer();
        try {
            int len;
            while ((len = mIn.read(buf)) != -1) {

                buf.flip();
                hexDecoder.decode(buf, charBuf, false);

                charBuf.put('|');

                buf.flip();
                asciiDecoder.decode(buf, charBuf, false);

                charBuf.put('\n');

                writeBuf.flip();
                mOut.write(writeBuf);
                charBuf.clear();
                buf.clear();
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
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
