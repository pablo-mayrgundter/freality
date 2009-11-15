package web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

/**
 * A FilenameFilter that accepts only configured directories.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class DirectoryFilter implements FilenameFilter {

    final String mConfigureDirFilename;
    final Set<String> mDirNamesToIgnore;

    /**
     * Constructs a DirectoryFilter with the specified configured
     * directory indicator file.
     *
     * @param configuredDirectoryFile Indicates that a file of
     * the given name must be present for the directory it is in
     * to be accepted by this filter.
     */
    DirectoryFilter(final String configuredDirectoryFilename, final Set<String> dirNamesToIgnore) {
        mConfigureDirFilename = configuredDirectoryFilename;
        mDirNamesToIgnore = dirNamesToIgnore;
    }
    DirectoryFilter(final String configuredDirectoryFile) {
        this(configuredDirectoryFile, new java.util.HashSet<String>());
    }
    
    public boolean accept(File dir, String name) {
        final File f = new File(dir.getPath() + File.separator + name);
        if (mDirNamesToIgnore.contains(f.getName()))
            return false;
        if (!f.isDirectory())
            return false;
        for (final String filename : f.list())
            if (filename.equals(mConfigureDirFilename))
                return true;
        return false;
    }
}
