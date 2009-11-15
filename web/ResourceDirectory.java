package web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * The ResourceDirectory class facilitates access to a structured
 * directory.  A structured directory is a directory that has an
 * expected set of files, and resources associated with those files.
 *
 * This class currently associates a String with the occurence of one
 * or more type of file in a directory, as specified by a
 * FilenameFilter.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1 $
 */
class ResourceDirectory {

    final FilenameFilter filter;
    final String title;
    final String description;
    
    ResourceDirectory(FilenameFilter filter, String title, String description) {
        this.filter = filter;
        this.title = title;
        this.description = description;
    }
    
    final String [] thingsToList(final File listRoot) {
        final String [] things = listRoot.list(filter);
        System.out.println("path: " + listRoot);
        Arrays.sort(things);
        return things;
    }
}
