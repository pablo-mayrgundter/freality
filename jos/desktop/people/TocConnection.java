package jos.desktop.people;

import com.wilko.jaim.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jos.desktop.Utils;

class TocConnection implements JaimEventListener {

    final Groups mGroups;
    /**
     * The connection is statically shared among all concurrent chats.
     */
    JaimConnection con = null;
    boolean available = false;

    final Map<String, Chat> chats;

    TocConnection(Groups groups) {
        chats = new HashMap<String, Chat>();
        if (con == null)
            con = new JaimConnection();
        //        con = new JaimConnection("toc.oscar.aol.com",9898);
        mGroups = groups;
    }

    void startChat(String buddy, Chat chat) {
        chats.put(buddy.toLowerCase(), chat);
    }

    void endChat(String buddy) {
        chats.remove(buddy);
    }

    /**
     * Connect and disconnect based on current availabiltity.
     * @return The new availability state.
     */
    synchronized boolean setAvailable(boolean available) {
        if (this.available) {
            if (!available)
                disconnect();
        } else {
            if (this.available)
                connect();
        }
        return this.available;
    }

    void connect() {

        String screenname = Utils.getUserPref("chat.screenname");
        if (screenname == null) {
            // prompt.
            screenname = "pabloMayrgundter";
        }
        String password = Utils.getUserPref("chat.password");
        if (password == null) {
            // prompt.
            password = "asdf;lkj";
        }

        try {
            con.setDebug(true);
            con.connect();
            con.addEventListener(this);
            con.watchBuddy("pabloMayrgundter"); // Hack.
            con.logIn(screenname, password, 5000);
            con.addBlock("");
            con.setInfo("Testing freality.org/~pablo/code/jos Chat client");
            available = true;
        } catch (JaimException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void disconnect() {
        try {
            con.disconnect();
            available = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendIM(String buddyName, String msg) {
        try {
            con.sendIM(buddyName, msg, false);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Receive an event and process it according to its content
     * @param event - The JaimEvent to be processed
     */
    public void receiveEvent(JaimEvent event) {
        TocResponse tr=event.getTocResponse();
        String responseType=tr.getResponseType();
        if (responseType.equalsIgnoreCase(BuddyUpdateTocResponse.RESPONSE_TYPE)) {
            receiveBuddyUpdate((BuddyUpdateTocResponse)tr);
        } else if (responseType.equalsIgnoreCase(IMTocResponse.RESPONSE_TYPE)) {
            final IMTocResponse rsp = (IMTocResponse) tr;
            final String buddyName = rsp.getFrom();
            Chat chat = chats.get(buddyName.toLowerCase());
            System.err.println("msg from: " + buddyName + ": " + rsp);
            if (chat == null) {
                chat = new Chat(buddyName, this);
                chats.put(buddyName.toLowerCase(), chat);
            }
            chat.receiveIM(rsp.getFrom(), rsp.getMsg());
        } else if (responseType.equalsIgnoreCase(EvilTocResponse.RESPONSE_TYPE)) {
            receiveEvil((EvilTocResponse)tr);
        } else if (responseType.equalsIgnoreCase(GotoTocResponse.RESPONSE_TYPE)) {
            receiveGoto((GotoTocResponse)tr);
        } else if (responseType.equalsIgnoreCase(ConfigTocResponse.RESPONSE_TYPE)) {
            receiveConfig();
        } else if (responseType.equalsIgnoreCase(ErrorTocResponse.RESPONSE_TYPE)) {
            receiveError((ErrorTocResponse)tr);
        } else if (responseType.equalsIgnoreCase(LoginCompleteTocResponse.RESPONSE_TYPE)) {
            System.out.println("Login is complete");
        } else if (responseType.equalsIgnoreCase(ConnectionLostTocResponse.RESPONSE_TYPE)) {
            System.out.println("Connection lost!");
        } else {
            System.out.println("Unknown TOC Response:"+tr.toString());
        }
    }

    private void receiveError(ErrorTocResponse et) {
        System.out.println("Error: "+et.getErrorDescription());
    }
    
    private void receiveEvil(EvilTocResponse er) {
        if (er.isAnonymous()) {
            System.out.println("We have been warned anonymously!");
        }
        else {
            System.out.println("We have been warned by "+er.getEvilBy());
            /*
              try {
              con.sendEvil(er.getEvilBy(),false);     // Let's warn them back
              con.addBlock(er.getEvilBy());          // And block them
              }
              catch (Exception e) {
              e.printStackTrace();
              }
            */
        }
        
        System.out.println("New warning level is:"+er.getEvilAmount());
    }
    
    private void receiveGoto(GotoTocResponse gr) {
        System.out.println("Attempting to access "+gr.getURL());
        try {
            InputStream is = con.getURL(gr.getURL());
            if (is!=null) {
                InputStreamReader r=new InputStreamReader(is);
                int chr=0;
                while (chr!=-1) {
                    chr=r.read();
                    System.out.print((char)chr);
                }
                
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void receiveConfig() {
        System.out.println("Config is now valid.");
        try {
            final Iterator groupItr = con.getGroups().iterator();
            while (groupItr.hasNext()) {
                final Group group = (Group) groupItr.next();
                System.out.println("Group: " + group.getName());
                final Enumeration buddyEnum = group.enumerateBuddies();
                while (buddyEnum.hasMoreElements()) {
                    final Buddy buddy = (Buddy) buddyEnum.nextElement();
                    buddy.setDeny(false);
                    buddy.setPermit(false);
                    con.watchBuddy(buddy.getName());
                    if (buddy.getDeny()) {
                        con.addBlock(buddy.getName());
                    }
                    if (buddy.getPermit()) {
                        con.addPermit(buddy.getName());
                    }
                    mGroups.addPerson(buddy.getName());
                }
            }
            con.saveConfig();
        }
        catch (Exception je) {
            je.printStackTrace();
        }
    }

    private void receiveBuddyUpdate(BuddyUpdateTocResponse bu) {

        System.out.println("Buddy update: "+bu.getBuddy());

        if (bu.isOnline())
            System.out.println("Online");
        else
            System.out.println("Offline");
        
        if (bu.isAway())
            System.out.println("Away");
        
        System.out.println("evil: "+bu.getEvil());
        System.out.println("Idle: "+bu.getIdleTime());
        System.out.println("On since "+bu.getSignonTime().toString());
    }
}
