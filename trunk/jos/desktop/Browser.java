package jos.desktop;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.activation.DataHandler;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import jos.util.Debug;

/**
 * A simple web browser.
 *
 * To run standalone: java jos.desktop.Browser
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>.
 * @version $Revision: 1.1.1.1 $
 */
class Browser extends Application implements ActionListener, HyperlinkListener {

    static { CLASS = Browser.class.getName(); } // HACK: Enables standalone.
    public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";

    final JButton mBackButton;
    final JButton mForwardButton;
    final JButton mRefreshButton;

    final JTextField mAddressBar;

    final JPanel mPanel;
    final JEditorPane mDocDisplay;
    final JScrollPane mDocScrollPane;
    final JPanel mImgDisplay;
    final JScrollPane mImgScrollPane;
    final CardLayout mCards;

    final MyVector<String> mHistory;
    int mHistoryCurPos;
    int mHistoryNewestPos;

    Browser() {
        super(Utils.getRsrcProp("browser.name") + " " + CVS_VERSION.split(" ")[1]);
        setSize(400, 600);
        Box navBar = new Box(BoxLayout.X_AXIS);

        navBar.add(mBackButton = Utils.makeSimpleButton(Utils.getRsrcProp("browser.back"),
                                                        80, 60, this, "back", false));
        navBar.add(mForwardButton = Utils.makeSimpleButton(Utils.getRsrcProp("browser.forward"),
                                                           80, 60, this, "forward", false));
        navBar.add(mRefreshButton = Utils.makeSimpleButton(Utils.getRsrcProp("browser.refresh"),
                                                           80, 60, this, "refresh", false));

        mAddressBar = new JTextField();
        mAddressBar.addActionListener(this);
        navBar.add(mAddressBar);

        getContentPane().add(navBar, java.awt.BorderLayout.NORTH);

        mDocDisplay = new JEditorPane();
        mDocDisplay.setEditable(false);
        mDocDisplay.addHyperlinkListener(this);
        mDocScrollPane = new JScrollPane(mDocDisplay);

        mImgDisplay = new JPanel();
        mImgScrollPane = new JScrollPane(mImgDisplay);

        mCards = new CardLayout();
        //    mPanel = new JPanel(mCards);
        mPanel = new JPanel();
        //    mPanel.add(mDocScrollPane);
        //    mPanel.add(mDocScrollPane, "doc");
        //    mPanel.add(mImgScrollPane, "img");

        getContentPane().add(mDocScrollPane);
        //    getContentPane().add(mImgScrollPane);
        //    mCards.show(mPanel, "doc");

        mHistory = new MyVector<String>();
        mHistoryCurPos = -1;
        mHistoryNewestPos = -1;
    }

    class MyVector<E> extends Vector<E> {
        public void removeRange(int from, int to) { super.removeRange(from, to); }
    }

    void updateHistory(String url) {
        mHistoryNewestPos = ++mHistoryCurPos;
        mHistory.removeRange(mHistoryCurPos, mHistory.size());
        mHistory.add(url);
        mRefreshButton.setEnabled(true);
        if (mHistoryCurPos > 0)
            mBackButton.setEnabled(true);
    }

    void back() throws IOException {
        if (mHistoryCurPos > 0) {
            mHistoryCurPos--;
            mDocDisplay.setPage((String) mHistory.get(mHistoryCurPos));
            mForwardButton.setEnabled(true);
        }
        if (mHistoryCurPos <= 0)
            mBackButton.setEnabled(false);
    }

    void forward() throws IOException {
        if (mHistoryCurPos + 1 < mHistory.size()) {
            mHistoryCurPos++;
            mDocDisplay.setPage((String) mHistory.get(mHistoryCurPos));
            mBackButton.setEnabled(true);
        }
        if (mHistoryCurPos + 1 >= mHistory.size())
            mForwardButton.setEnabled(false);
        mHistoryNewestPos = Math.max(mHistoryCurPos, mHistoryNewestPos);
    }

    void refresh() throws IOException {
        mDocDisplay.setPage((String) mHistory.get(mHistoryCurPos));
    }

    public void actionPerformed(ActionEvent e) {
        String urlStr = e.getActionCommand();
        if (urlStr.indexOf(".") == -1) {
            urlStr += ".com";
        }
        if (urlStr.indexOf("://") == -1) {
            urlStr = "http://" + urlStr;
        }
        boolean success = true;
        try {
            final URL url = new URL(urlStr);
            final DataHandler handler = new DataHandler(url);
            final String contentType = handler.getContentType();
            if (contentType.startsWith("text/html")
                || contentType.startsWith("text/plain")
                || contentType.startsWith("text/rtf")) {
                mDocDisplay.setPage(url);
                //        mCards.show(mPanel, "doc");
            } else {
                if (contentType.startsWith("image")) {
                    final BufferedImage img = ImageIO.read(url);
                    mImgDisplay.imageUpdate(img, 0, 0,
                                            img.getWidth(mImgDisplay),
                                            img.getHeight(mImgDisplay),
                                            0);
                }
            }
        } catch (java.net.UnknownHostException uhe) {
            assert Debug.trace(uhe);
            mAddressBar.setText(Utils.getRsrcProp("browser.unknownHost") + urlStr);
        } catch (IOException ioe) {
            assert Debug.trace(ioe);
            mAddressBar.setText(Utils.getRsrcProp("browser.pageLoadErr"));
            success = false;
        }
        if (success) updateHistory (urlStr);
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent event = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
            } else {
                boolean success = true;
                final java.net.URL pageURL = e.getURL();
                try {
                    pane.setPage(pageURL);
                    mAddressBar.setText(pageURL.toString());
                } catch (Exception exc) {
                    assert Debug.trace(exc);
                    success = false;
                }
                if (success) updateHistory (pageURL.toString());
            }
        }
    }
}

    /*
      Font WEB_PAGE_FONT = new Font("verdanaasdfasdf", Font.BOLD, 14);

      // mDocDisplay.setFont(WEB_PAGE_FONT);
      final StyleSheet ss = ((HTMLDocument) mDocDisplay.getDocument()).getStyleSheet();
    try {
      ss.importStyleSheet(new java.net.URL("http://freality.org/main.css"));
    } catch (Exception e) {
      e.printStackTrace();
      }*/


