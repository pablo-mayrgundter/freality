package web;

/**
 * The Documentation class contains utilities for finding, reading and
 * writing directory documentation.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.2 $
 */
public class Documentation {

    /**
     * Extracts from the given source String the contents of the given
     * tagName only.
     */
    public static final String innerHTML(final String tagName, final String source) {
        return innerHTML(tagName, new StringBuffer(source));
    }
    public static final String innerHTML(final String tagName, final StringBuffer source) {
        int start = source.indexOf("<" + tagName);
        if (start == -1) return "";
        int stop = source.indexOf(">", start + 1);
        if (stop == -1) return "";
        source.delete(0, stop + 1);
        start = source.indexOf("</" + tagName + ">");
        if (start == -1) return "";
        source.delete(start, source.length());
        return source.toString();
    }
}
