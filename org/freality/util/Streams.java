package org.freality.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Common stream utilities.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class Streams {

    public static final byte [] readFully(File f) throws IOException {
        final FileInputStream fis = new FileInputStream(f);
        try {
            return readFully(fis);
        } finally {
            fis.close();
        }
    }

    public static final byte [] readFully(InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte [] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) != -1)
            baos.write(buf, 0, len);
        return baos.toByteArray();
    }
}
