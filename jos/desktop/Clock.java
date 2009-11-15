package jos.desktop;

import javax.swing.JLabel;

/**
 * A simple menubar clock.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class Clock extends JLabel implements Runnable {
    public void run() {
        while (true) {
            setText(new java.util.Date().toString());
            try {
                Thread.currentThread().sleep(1000);
            } catch(InterruptedException e) { break; }
        }
    }
}
