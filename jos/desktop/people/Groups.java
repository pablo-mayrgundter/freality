package jos.desktop.people;

import com.wilko.jaim.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jos.desktop.Application;
import jos.desktop.Desktop;

/**
 * A simple instant messenger.  Uses the TOC network view Jaimlib.
 *
 * To run standalone: java jos.desktop.people.Groups
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Groups extends Application implements ListSelectionListener {

    TocConnection mTocConnection = null;
    final JList mPeople;
    
    public Groups() {
        super("Groups");
        setSize(200, 200);
        DefaultListModel listModel = new DefaultListModel();
        mPeople = new JList(listModel);
        mPeople.addListSelectionListener(this);
        mPeople.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mPeople.setVisibleRowCount(20);
        final JScrollPane scrollPane = new JScrollPane(mPeople);
        add(scrollPane);
        getConnection();
    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            final int ndx = mPeople.getSelectedIndex();
            mPeople.ensureIndexIsVisible(ndx);
            final String name = (String) mPeople.getModel().getElementAt(ndx);
            final Chat chat = new Chat(name, mTocConnection);
            mTocConnection.chats.put(name.toLowerCase(), chat);
            Desktop.getDesktop().addApp(chat);
        }
    }

    void addPerson(String name) {
        ((DefaultListModel) mPeople.getModel()).addElement(name);
    }

    void delPerson(String name) {
        ((DefaultListModel) mPeople.getModel()).removeElement(name);
    }

    TocConnection getConnection() {
        if (mTocConnection == null) {
            mTocConnection = new TocConnection(this);
            mTocConnection.connect();
        }
        return mTocConnection;
    }

}
