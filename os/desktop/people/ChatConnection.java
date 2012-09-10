package os.desktop.people;

import java.util.HashMap;
import java.util.Map;

class ChatConnection {

  Map<String, Chat> chats = new HashMap<String, Chat>();
  Groups groups;

  ChatConnection(Groups g) {
    groups = g;
  }

  public void connect() {
  }

  public void endChat(String buddyName) {
  }

  public boolean sendIM(String buddyName, String msg) {
    return false;
  }
}
