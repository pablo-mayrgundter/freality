package os.bin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Grep implementation using Java regex.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1 $
 */
public class Grep extends UNIXProgram implements Runnable {

    final Matcher mMatcher;

    Grep(final Matcher matcher, final Object inputType, final Object outputType) throws IOException {
        super(inputType, outputType);
        mMatcher = matcher;
    }

    public void run() {
        String line;
        final ByteBuffer buf = ByteBuffer.allocateDirect(4096);
        final CharBuffer charView = buf.asCharBuffer();
        try {
            int i, n;
            while ((n = mIn.read(buf)) != -1) {
                buf.flip();
                n = n / 2;
                for (i = 0; i < n; i++)
                    if (charView.get(i) == '\n') {
                        charView.limit(i);
                        buf.limit(i*2);
                        mMatcher.reset(charView);
                        buf.mark();
                        if (mMatcher.find())
                            mOut.write(buf);
                        buf.reset();
                        charView.position(i);
                        buf.position(i*2);
                    }
                buf.clear();
                charView.clear();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String [] args) throws IOException {
        Grep grep = null;
        switch (args.length) {
        case 1: grep = new Grep(Pattern.compile(args[0]).matcher(""), System.in, System.out); break;
        case 2: grep = new Grep(Pattern.compile(args[0]).matcher(""), new File(args[1]), System.out); break;
        default: {
            System.err.printf("Usage: java %s PATTERN [FILE]\n", Grep.class.getName());
            System.exit(1);
        }
        }
        grep.run();
    }
}
