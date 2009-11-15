package gfx.vt;

/**
 * Enumeration of the VT100 Escape Sequences.
 *
 * http://pegasus.cs.csubak.edu/Tables_Charts/VT100_Escape_Codes.html
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.3 $
 */
public final class VT100 {

    public static final String RESET_DEVICE = "\033[c";
    public static final String ENABLE_LINE_WRAP = "\033[7h";
    public static final String DISABLE_LINE_WRAP = "\033[7l";
    public static final String CLEAR_SCREEN = "\033[2J";

    public static final String CURSOR_TO_APP   = "\033[?1h";
    public static final String CURSOR_HOME     = "\033[;H";
    public static final String CURSOR_SAVE     = "\033[s";
    public static final String CURSOR_RESTORE  = "\033[u";
    public static final String CURSOR_UP       = "\033[A";
    public static final String CURSOR_DOWN     = "\033[B";
    public static final String CURSOR_FORWARD  = "\033[C";
    public static final String CURSOR_BACKWARD = "\033[D";

    public static final String ERASE_TO_EOL = "\033[K";

    public static final String FG_BLACK   = "\033[0;30m";
    public static final String FG_RED     = "\033[0;31m";
    public static final String FG_GREEN   = "\033[0;32m";
    public static final String FG_YELLOW  = "\033[0;33m";
    public static final String FG_BLUE    = "\033[0;34m";
    public static final String FG_MAGENTA = "\033[0;35m";
    public static final String FG_CYAN    = "\033[0;36m";
    public static final String FG_WHITE   = "\033[0;37m";

    public static final String BG_BLACK   = "\033[0;40m";
    public static final String BG_RED     = "\033[0;41m";
    public static final String BG_GREEN   = "\033[0;42m";
    public static final String BG_YELLOW  = "\033[0;43m";
    public static final String BG_BLUE    = "\033[0;44m";
    public static final String BG_MAGENTA = "\033[0;45m";
    public static final String BG_CYAN    = "\033[0;46m";
    public static final String BG_WHITE   = "\033[0;47m";

    public static String cursorUp(final int count) {
        return "\033["+count+"A";
    }

    public static String cursorDown(final int count) {
        return "\033["+count+"B";
    }

    public static String cursorForward(final int count) {
        return "\033["+count+"C";
    }

    public static String cursorBackward(final int count) {
        return "\033["+count+"D";
    }

    public static String cursorForce(final int row, final int col) {
        return "\033["+row+";"+col+"f";
    }

    public static String cursorDirect(final int row, final int col) {
        return "\033["+row+";"+col+"H";
    }

    /** Moves cursor to given location, prints given status msg, and
     * then restores cursor's previous location. */
    public static String statusMsg(final int row, final int col, final String msg) {
        return CURSOR_SAVE + cursorForce(row, col) + msg + CURSOR_RESTORE;
    }
}
