package bin;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * NetCat extends Cat to accept network socket streams.
 *
 * @author Pablo Mayrgundter
 */
public class Nc extends Cat {

  final String hostname;
  final int port;

  Nc(String hostname, int port, Reader r, PrintStream p) throws IOException {
    super(r, p);
    this.hostname = hostname;
    this.port = port;
  }
    
  public void run() {
    // TODO(pablo)
  }

  public static void main(String [] args) throws IOException {
    // TODO(pablo)
  }
}
