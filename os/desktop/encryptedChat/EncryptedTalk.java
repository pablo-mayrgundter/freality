import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;

/**
 * A simple chat app, with DES encryption.  Communicates on port 10000.
 *
 * To use, the first person types:
 *
 *   java EncryptedTalk
 *
 * the second person types:
 *
 *   java EncryptedTalk <first person's server name or IP address>
 *
 * and you'll probably have to type Control-C to quit.
 *
 * Each client instantiates two instances of this class.  One handles
 * an outgoing stream, the other an incoming stream:
 *
 * OUTGOING:
 * System.in -> Encrypt -> Socket.out
 *
 * INCOMING:
 * Socket.in -> Decrypt -> System.out
 *
 * The encryption is based on a shared key, which is hardcoded.  This
 * should be replaced in the future with Public-Key, but will still
 * prevent typical network sniffing.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
class EncryptedTalk implements Runnable {

  static final int DEFAULT_SERVER_PORT = 10000; 

  final InputStream mIn;
  final OutputStream mOut;
  boolean mOutgoing;

  EncryptedTalk(InputStream is, OutputStream os, boolean outgoing) throws Exception {
    mOutgoing = outgoing;
    KeyGenerator kg = KeyGenerator.getInstance("DESede");
    kg.init(new SecureRandom(new byte[]{'P','A','B','L','O',' ','I','S','T',' ','R','A','D'}));
    Key key = kg.generateKey();

    Cipher encryptCipher = Cipher.getInstance("DESede");
    encryptCipher.init(Cipher.ENCRYPT_MODE, key);

    Cipher decryptCipher = Cipher.getInstance("DESede");
    decryptCipher.init(Cipher.DECRYPT_MODE, key);

    if (mOutgoing) {
      mIn = is;
      mOut = new CipherOutputStream(os, encryptCipher);
    } else {
      mIn = new CipherInputStream(is, decryptCipher);
      mOut = os;
    }
  }

  void zero(byte [] bytes) { java.util.Arrays.fill(bytes, (byte)0); }

  public void run() {
    // A somewhat convoluted i/o scheme is used here to ensure buffers
    // are flushed.
    try {
      final byte [] bytes = new byte[1024];
      int ret;
      zero(bytes);
      while((ret = mIn.read(bytes)) != -1) {
        if (mOutgoing) {
          mOut.write('>');
          mOut.write(' ');
        }
        mOut.write(bytes, 0, bytes.length);
        mOut.flush();
        zero(bytes);
      }
      System.out.println("Connection closed.");
      mIn.close();
      mOut.close();
    } catch(Exception e) {
      System.err.println("Error in communication: " + e);
    }
  }

  public static void main(String [] args) {
    EncryptedTalk incoming = null;
    EncryptedTalk outgoing = null;
    try {

      Socket s = null;

      if (args.length == 0) {
        System.out.println("Listening...");
        s = new ServerSocket(DEFAULT_SERVER_PORT).accept();
      } else {
        System.out.println("Calling...");
        s = new Socket(args[0], DEFAULT_SERVER_PORT);
      }
      System.out.println("Connected!");
      incoming = new EncryptedTalk(s.getInputStream(), System.out, false);
      outgoing = new EncryptedTalk(System.in, s.getOutputStream(), true);
    } catch (Exception e) {
      System.err.println("Couldn't initialize: " + e);
      return;
    }
    new Thread(incoming).start();
    new Thread(outgoing).start();
  }
}
