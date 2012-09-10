package os.desktop.people;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import os.desktop.Application;
import os.desktop.Desktop;
import os.desktop.Utils;
import os.util.Debug;

/**
 * A chat with a person.
 *
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
@SuppressWarnings(value="serial")
class Chat extends Application {

  final JTextArea mDisplay;
  final JTextArea mEntry;
  final StringBuffer mSendBuffer = new StringBuffer();
  final ChatConnection mConnection;
  Thread mSendingThread = null;
  String mBuddyName = null;

  Chat(String buddyName, ChatConnection connection) {
    super(Utils.getRsrcProp("chat.name") + " " + CVS_VERSION.split(" ")[1]);

    mBuddyName = buddyName;

    setSize(350, 300);
    final Box buddyPanel = new Box(BoxLayout.X_AXIS);
    buddyPanel.add(new JLabel(Utils.getRsrcProp("chat.buddy")));
    getContentPane().add(buddyPanel, BorderLayout.NORTH);

    mDisplay = new JTextArea();
    mDisplay.setEditable(false);
    mDisplay.setLineWrap(true);
    mEntry = new JTextArea();
    mEntry.setLineWrap(true);
    final  JSplitPane chatPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                                                new JScrollPane(mEntry),
                                                new JScrollPane(mDisplay));
    new EntryListener(mEntry);
    chatPane.setSize(350, 300);
    chatPane.setDividerLocation(150);
    getContentPane().add(chatPane);

    startSendBufferThread();
    mConnection = connection;
  }

  void startSendBufferThread() {
    mSendingThread = new Thread(new Runnable() {
        public void run() {
          infin:
          while (true) {
            synchronized (mSendBuffer) {

              while (mSendBuffer.length() == 0) {
                try {
                  mSendBuffer.wait();
                } catch (InterruptedException e) {
                  break infin;
                }
              }

              send(mSendBuffer.toString());
              mSendBuffer.setLength(0);
            }
          }
        }
      });
    mSendingThread.start();
  }

  public void dispose() {
    mSendingThread.interrupt();
    assert Debug.println("de-registering with ChatConnection...");
    mConnection.endChat(mBuddyName);
    super.dispose();
  }
  
  
  void showStatus(String msg) {
    mDisplay.insert(msg, 0);
  }
  void appendConveration(String who, String said) {
    mDisplay.insert(who + ": " + said.replaceAll("<[^<>]*>", "") + "\n", 0);
  }
    
  void receiveIM(String from, String msg) {
    appendConveration(from, msg);
  }

  void send(String msg) {
    mEntry.setText("");
    boolean success = false;
    try {
      success = mConnection.sendIM(mBuddyName, msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (success)
      appendConveration("FIXME", msg);
    else
      showStatus("An error occurred sending the message.");
  }

  class EntryListener implements DocumentListener {

    final JTextArea mEntryArea;

    EntryListener(JTextArea textArea) {
      mEntryArea = textArea;
      mEntryArea.getDocument().addDocumentListener(this);
    }

    public void insertUpdate(DocumentEvent e) {
      String enteredText = mEntryArea.getText();
      if (enteredText.length() == 0) return;
      if (!enteredText.endsWith("\n")) return;
      synchronized (mSendBuffer) {
        mSendBuffer.append(enteredText.trim());
        mSendBuffer.notify();
      }
    }

    public void changedUpdate(DocumentEvent e) {}
    public void removeUpdate(DocumentEvent e) {}
  }

  static { CLASS = Chat.class.getName(); } // HACK: Enables standalone.
  public static final String CVS_VERSION = "$Revision: 1.1.1.1 $";
}
