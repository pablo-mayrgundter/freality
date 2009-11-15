package util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Grep implementation using Java regex.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
//public class Grep extends UNIXProgram implements Runnable {
public class Grep implements Runnable {

    final Matcher mMatcher;

    Grep(final Matcher matcher, final Object inputType, final Object outputType) throws IOException {
        //        super(inputType, outputType);
        mMatcher = matcher;
    }

    public void run() {
        /*
        int lineCount = 0;
        String line;
            /*
        try {
            while ((line = mInReader.readLine()) != null) {
                mMatcher.reset(line);
                if (mMatcher.find())
                    mOutStream.printf("%s\n", line);
                lineCount++;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        */
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
