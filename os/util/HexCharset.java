package os.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class HexCharset extends Charset {

    /** Hex digits. */
    static final char [] DIGITS = new char[] {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'
    };

    public HexCharset() {
        super("HexData", null);
    }

    class HexDecoder extends CharsetDecoder {

        protected HexDecoder(final Charset cs) {
            super(cs, 2, 2);
        }

        public final CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
            for (int i = in.position(), n = in.remaining(); i < n; i++) {
                final byte b = in.get();
                out.put(DIGITS[b >>> 4  & 0x0F]);
                out.put(DIGITS[b        & 0x0F]);
            }
            return CoderResult.UNDERFLOW;
        }
    }

    // Abstract methods in Charset.

    public final boolean contains(final Charset cs) {
        return cs.equals(this);
    }

    public final CharsetDecoder newDecoder() {
        return new HexDecoder(this);
    }

    public boolean canEncode() {
        return false;
    }

    /** Not implemented. */
    public final CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException();
    }

    public static void main(String [] args) {
        final ByteBuffer buf = ByteBuffer.wrap(args[0].getBytes());
        final HexCharset hc = new HexCharset();
        System.out.println(hc.decode(buf));
    }
}
