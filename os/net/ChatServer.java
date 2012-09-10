package os.net;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import os.util.Debug;

/**
 * The ChatServer maintains a list of online buddies.  Here is the protocol:
 *
 * CLIENT                     SERVER
 * init {
 *   protoVersion         ->
 *                            if (protoVersion == myProtoVersion)
 *                        <-    myProtoVersion
 *                            else
 *                              disconnect
 *   signon [name, port]  ->  add to signed-on list // Could do auth here
 * }
 *
 * if (init) sessionLoop {
 *   rqstBuddyInfo [name] ->
 *                            if (signed-on list contains name)
 *                        <-    buddyInfo [name, IP, port]
 *                            else
 *                        <-    buddyOffline [name]
 * }
 *
 * if (init) end {
 *   signoff [name]       ->  remove [name] from signed-on list
 * }
 */
public class ChatServer {

  public static final String DEFAULT_HOST = System.getProperty("os.net.ChatServer.host", "freality.org");
  public static final int DEFAULT_PORT = Integer.parseInt(System.getProperty("os.net.ChatServer.port", "13337"));

  /** exactly "CHAT PROTOCOL VERSION 1.0" */
  public static final String PROTO_VERSION = "CHAT PROTOCOL VERSION 1.0";

  /** e.g. "SIGNON name port" */
  public static final String PROTO_SIGNON = "SIGNON";

  /** e.g. "SIGNOFF name" */
  public static final String PROTO_SIGNOFF = "SIGNOFF";

  /**  e.g. "BUDDY_REQUEST_INFO name" */
  public static final String PROTO_REQUEST_BUDDY_INFO = "BUDDY_REQUEST_INFO";

  /**  e.g. "BUDDY_INFO name ip port" */
  public static final String PROTO_RESPONSE_BUDDY_INFO = "BUDDY_INFO";

  /**  e.g. "BUDDY_OFFLINE name" */
  public static final String PROTO_RESPONSE_BUDDY_OFFLINE = "BUDDY_OFFLINE";

  public static final Map<String, String> ONLINE_USERS = new HashMap<String, String>();

  public static void main(String [] args) {
    new Thread(new Runnable() {
        
        PrintWriter CLIENT_WRITER = null;
        BufferedReader CLIENT_READER = null;

        void send(String msg) throws IOException {
          assert Debug.println("SEND: " + msg);
          CLIENT_WRITER.println(msg);
          CLIENT_WRITER.flush();
        }

        String recv() throws IOException {
          final String msg = CLIENT_READER.readLine();
          assert Debug.println("RECV: " + msg);
          return msg;
        }

        public void run() {
          try {
            final ServerSocket s = new ServerSocket(DEFAULT_PORT);
            System.err.println("ChatServer: listening on: " + DEFAULT_PORT);
            while (true) {
              final Socket sessionSocket = s.accept();
              System.err.println("ChatServer: Client connected from: "
                                 + sessionSocket.getRemoteSocketAddress());
              // Keep this for bookkeeping.
              final InetSocketAddress remoteSocketAddr =
                (InetSocketAddress) sessionSocket.getRemoteSocketAddress();

              // Manage each buddy connection in parallel.
              new Thread(new Runnable() {
                  public void run() {
                    String buddy = null;
                    boolean signedon = false;
                    try {
                      CLIENT_WRITER =
                        new PrintWriter(new OutputStreamWriter(sessionSocket.getOutputStream()));
                      CLIENT_READER =
                        new BufferedReader(new InputStreamReader(sessionSocket.getInputStream()));
                      String line = recv();
                      // The only consistency check.
                      if (line != null && line.equals(PROTO_VERSION)) {
                        send(PROTO_VERSION);
                        while ((line = recv()) != null) { // Loop through commands.
                          if (line.startsWith(PROTO_SIGNON)) {
                            // e.g. "SIGNON name port"
                            final String [] parts = line.split(" ");
                            buddy = parts[1];
                            signedon = true;
                            ONLINE_USERS.put(buddy,
                                             remoteSocketAddr.getAddress().getHostAddress()
                                             + " " + parts[2]);
                          } else if (signedon && line.startsWith(PROTO_SIGNOFF)) {
                            signedon = false;
                            break;
                          } else if (signedon && line.startsWith(PROTO_REQUEST_BUDDY_INFO)) {
                            final String [] parts = line.split(" ");
                            final String requestBuddy = parts[1];
                            final String buddySocketAddr = ONLINE_USERS.get(requestBuddy);
                            if (buddySocketAddr != null) {
                              send(PROTO_RESPONSE_BUDDY_INFO
                                   + " " + requestBuddy
                                   + " " + buddySocketAddr);
                            } else {
                              send(PROTO_RESPONSE_BUDDY_OFFLINE);
                            }
                          }
                        }
                        // Exit the while from either a dropped
                        // connection or an explicit signoff.  Either
                        // way, user is no longer avail.
                        ONLINE_USERS.remove(buddy);
                      } else { // if ! known proto version
                        // Should do some better logging here.
                        System.err.println("Connection dropped or protocol problems: " + line);
                      }
                      CLIENT_READER.close();
                      CLIENT_WRITER.close();
                    } catch (Exception innerE) {
                      // Client connection died.
                      assert Debug.trace(innerE);
                    }
                  }
                }).start();
            }
          } catch (Exception e) {
            // Server is down.
            assert Debug.trace(e);
          } // Should close server socket here?
        }
    }).start();
  }
}
