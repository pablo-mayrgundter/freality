package web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * The MimeFileFilter class filters files based on their canonical
 * extension.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class MimeFileFilter implements FilenameFilter {
        
    final Set<String> mIgnore;
    final Set<String> mPostfixes;
    
    public MimeFileFilter() {
        mIgnore = new HashSet<String>();
        mPostfixes = new HashSet<String>();
    }
    
    public void addFilenameToIgnore(final String filename) {
        mIgnore.add(filename);
    }
    public void addPostfixToAccept(final String postfix) {
        mPostfixes.add(postfix);
    }
    
    public boolean accept(File dir, String name) {
        final int ndx = name.lastIndexOf(".");
        if (ndx == -1 || ndx == name.length() - 1)
            return false;
        if (mPostfixes.contains(name.substring(ndx + 1)))
            return !mIgnore.contains(name);
        return false;
    }
}
