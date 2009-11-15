package web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The AnnotatedDirectoryView class facilitates access to a structured
 * directory.  A structured directory is a directory that has an
 * expected set of files, and resources associated with those files.
 *
 * This class currently associates a String with the occurence of one
 * or more type of file in a directory, as specified by a
 * FilenameFilter.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
public class AnnotatedDirectoryView {

    public static final String ANNOTATION_FILENAME = ".dirAnnotations.xml";

    public static final String SUBDIRS_PROP_REGEX = "subdirs";
    public static final String MIME_PROP_REGEX = "mime/.*";
    public static final String MIME_PTR_PROP_REGEX = "mime-ptr/.*";

    public final FilenameFilter filter;
    public final String title;
    public final String description;

    AnnotatedDirectoryView(FilenameFilter filter, String title, String description) {
        this.filter = filter;
        this.title = title;
        this.description = description;
    }
    
    public final String [] thingsToList(final File listRoot) {
        final String [] things = listRoot.list(filter);
        System.out.println("path: " + listRoot);
        Arrays.sort(things);
        return things;
    }

    public static AnnotatedDirectoryView [] load(final File annotationProps) throws IOException {
        final Properties props = new Properties();
        final FileInputStream fis = new FileInputStream(annotationProps);
        props.loadFromXML(fis);
        fis.close();
        final ArrayList<AnnotatedDirectoryView> views = new ArrayList<AnnotatedDirectoryView>();
        final Map<String, MimeFileFilter> mMimeFilters = new HashMap<String, MimeFileFilter>();
        for (final Object o : props.keySet()) {
            final String annotationKey = (String) o;
            final String annotationValue = (String) props.get(annotationKey);
            int ndx = annotationValue.indexOf(";");
            if (ndx == -1)
                throw new java.util.InvalidPropertiesFormatException("Incorrect format in annotation entry.");
            final String title = annotationValue.substring(0, ndx);
            final String description =
                ndx == annotationValue.length() - 1 ? "" : annotationValue.substring(ndx + 1);
            if (annotationKey.matches(SUBDIRS_PROP_REGEX)) {
                views.add(new AnnotatedDirectoryView(new DirectoryFilter("package.html"),
                                                     title, description));
            } else if (annotationKey.matches(MIME_PROP_REGEX)) {
                final String mimeType = annotationKey.split("/")[1];
                final MimeFileFilter ff = new MimeFileFilter();
                ff.addFilenameToIgnore(ANNOTATION_FILENAME);
                ff.addPostfixToAccept(mimeType);
                mMimeFilters.put(mimeType, ff);
                views.add(new AnnotatedDirectoryView(ff, title, description));
            } else if (annotationKey.matches(MIME_PTR_PROP_REGEX)) {
                final MimeFileFilter ff = mMimeFilters.get(title);
                if (ff == null)
                    throw new java.util.InvalidPropertiesFormatException("Pointer to unknown type.");
                ff.addPostfixToAccept(title);
            } else {
                throw new java.util.InvalidPropertiesFormatException("Unknown annotation entry type.");
            }
        }
        return views.toArray(new AnnotatedDirectoryView[]{});
    }
}
