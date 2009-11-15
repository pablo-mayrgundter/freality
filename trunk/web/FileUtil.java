package web;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * The FileUtil class contains utilities for reading and writing
 * files.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class FileUtil {

    private FileUtil() {}

    /**
     * Read the file at the given path and return it as a String.
     */
    public static final String fileToString(String path) {
        final StringBuffer buf = new StringBuffer();
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(path));
            String line;
            while((line = lnr.readLine()) != null) {
                buf.append(line);
                buf.append('\n');
            }
            lnr.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        return buf.toString();
    }
}
