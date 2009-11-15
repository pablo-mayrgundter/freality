package jos.bin;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * The UNIXProgram abstract class provides the common I/O facilities
 * that a typical UNIX program can use to generalize its input as
 * being from a file or input stream.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1 $
 */
public abstract class UNIXProgram {

    protected final ReadableByteChannel mIn;
    protected final WritableByteChannel mOut;

    protected UNIXProgram(final Object inputSource, final Object outputSink) throws IOException {

        if (inputSource instanceof File) {
            final RandomAccessFile raf = new RandomAccessFile((File) inputSource, "r");
            mIn = raf.getChannel();
        } else if (inputSource instanceof InputStream)
            mIn = Channels.newChannel((InputStream) inputSource);
        else
            throw new IllegalArgumentException("Unsupported input type: " + inputSource.getClass().getName());

        if (outputSink instanceof File) {
            final RandomAccessFile raf = new RandomAccessFile((File) outputSink, "rw");
            mOut = raf.getChannel();
        } else if (outputSink instanceof OutputStream)
            mOut = Channels.newChannel((OutputStream) outputSink);
        else
            throw new IllegalArgumentException("Unsupported output type: " + outputSink.getClass().getName());
    }
}
