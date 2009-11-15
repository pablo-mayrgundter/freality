package util;

import java.io.File;
import java.io.IOException;

/**
 * Simple HexDump implementation using Java Integer.toHexString()
 * formatting.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
//public class HexDump extends UNIXProgram implements Runnable {
public class HexDump implements Runnable {

    HexDump(final Object inputType, final Object outputType) throws IOException {
        //        super(inputType, outputType);
    }

    public void run() {
        /*
        int lineCount = 0;
        String line;
        /*
        try {
            while ((line = mInReader.readLine()) != null) {
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        */
    }

    public static void main(String [] args) throws IOException {
        HexDump hd = null;
        switch (args.length) {
        case 1: hd = new HexDump(System.in, System.out); break;
        case 2: hd = new HexDump(new File(args[1]), System.out); break;
        default: {
            System.err.printf("Usage: java %s [file]\n", HexDump.class.getName());
            System.exit(1);
        }
        }
        hd.run();
    }
}
