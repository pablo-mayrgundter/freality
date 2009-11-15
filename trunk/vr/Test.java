package vr;

import org.freality.gui.three.Scene;
import org.freality.gui.three.Browser;

class Test {
    public static void main (final String [] args) {
        final Browser b = new Browser();
        final Scene scene = Boolean.getBoolean("test") ? new org.freality.gui.three.SimpleScene(b) : new vr.cpack.space.Scene(b);
        try { Thread.sleep(100); } catch (Exception e) {}
        b.setScene(scene);
    }
}
