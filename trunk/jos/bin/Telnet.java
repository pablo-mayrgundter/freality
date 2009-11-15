package jos.bin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Simple Telnet utility to connect to remote host, send data and
 * receive response.
 *
 * TODO: Make reading from stdin non-blocking.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1 $
 */
public class Telnet extends UNIXProgram implements Runnable {

    final String mHostname;
    final int mPort;

    Telnet(final String hostname, final int port,
           final Object inputType, final Object outputType) throws IOException {
        super(inputType, outputType);
        mHostname = hostname;
        mPort = port;
    }
    
    public void run() {
        try {
            final SocketChannel sc = SocketChannel.open(new InetSocketAddress(mHostname, mPort));
            sc.configureBlocking(false);
            final Selector selector = Selector.open();
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            final ByteBuffer buf = ByteBuffer.allocateDirect(4096);
            while (selector.isOpen()) {
                if (selector.select() == 0)
                    continue;

                final Iterator keyItr = selector.selectedKeys().iterator();
                while (keyItr.hasNext()) {

                    final SelectionKey key = (SelectionKey) keyItr.next();
                    keyItr.remove();

                    if (key.isWritable()) {
                        buf.clear();
                        mIn.read(buf);
                        buf.flip();
                        int len = 0;
                        while ((len += sc.write(buf)) < buf.limit())
                            buf.position(len);
                    }

                    if (key.isReadable()) {
                        buf.clear();
                        if (sc.read(buf) == -1) {
                            selector.close();
                            break;
                        }
                        buf.flip();
                        int len = 0;
                        while ((len += mOut.write(buf)) < buf.limit())
                            buf.position(len);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String [] args) throws IOException {
        Telnet call = null;
        switch (args.length) {
        case 1: call = new Telnet(args[0], 22, System.in, System.out); break;
        case 2: call = new Telnet(args[0], Integer.parseInt(args[1]), System.in, System.out); break;
        case 3: call = new Telnet(args[0], Integer.parseInt(args[1]),
                                   args[2].equals("-") ? System.in : new File(args[2]),
                                   System.out); break;
        case 4: call = new Telnet(args[0], Integer.parseInt(args[1]),
                                   args[2].equals("-") ? System.in : new File(args[2]),
                                   args[3].equals("-") ? System.out : new File(args[3])); break;
        default: {
            System.err.printf("Usage: java %s HOST [[[PORT] [SEND FILE | -]] [RECV FILE | -]]\n", Telnet.class.getName());
            System.exit(1);
        }
        }
        call.run();
    }
}
