package util;

/**
 * The Bits class implements a bitmask using a byte array.  Bits are
 * stored in Big-Endian order.  Instance methods assume a given size
 * of the underlying array for indexing safety.  Static methods are
 * also provided.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public final class Bits {

    final int mLength;
    final byte [] mBits;

    public Bits (final int length) {
        mLength = length;
        mBits = new byte[length / 8 + 1];
    }

    public int getLength () {
        return mLength;
    }

    /**
     * Sets all bits to 0.
     */
    public void clear () {
        fill(0);
    }

    public void fill (final int value) {
        fill(mBits, value);
    }

    /**
     * Sets all of the given bits to the given value.
     */
    public static void fill (final byte [] bits, final int value) {
        java.util.Arrays.fill(bits, value == 0 ? (byte) 0 : (byte) 0xFF);
    }

    /**
     * Returns the bit at the given index.
     *
     * @throws ArrayIndexOutOfBoundsException.
     */
    public int get (final int ndx) {
        return get(mBits, ndx, mLength);
    }

    /**
     * Sets the bit at the given index into the bits to the given
     * value.
     *
     * @throws ArrayIndexOutOfBoundsException.
     */
    public void set (final int ndx, final int value) {
        set(mBits, ndx, value, mLength);
    }

    /**
     * Sets the bit at the given index into the bits.
     *
     * @throws ArrayIndexOutOfBoundsException.
     */
    public void set (final int ndx) {
        set(ndx, 1);
    }

    public String toString () {
        return toString(mBits, mLength);
    }

    // STATIC METHODS

    public static int get (final byte [] bits, final int ndx) {
        return get(bits, ndx, bits.length);
    }


    public static int get (final byte [] bits, final int ndx, final int length) {
        final int block = ndx / 8;
        if (ndx < 0 || ndx >= length)
            throw new ArrayIndexOutOfBoundsException("Index: "+ ndx +", length: "+ length);
        assert block < length : "Index of "+ ndx +" goes to block "+ block +" of array length "+ length;
        final int offset = ndx - (block * 8);
        return get(bits[block], offset);
    }

    /**
     * Returns the bit at at the given offset from the given byte.
     *
     * @throws ArrayIndexOutOfBoundsException.
     */
    public static int get (final byte b, final int offset) {
        return (b >> offset) & 1;
    }

    /**
     * @throws ArrayIndexOutOfBoundsException.
     */
    public static void set (final byte [] bits, final int ndx, final int value) {
        set(bits, ndx, value, bits.length);
    }

    /**
     * @throws ArrayIndexOutOfBoundsException.
     */
    public static void set (final byte [] bits, final int ndx, final int value, final int length) {
        final int block = ndx / 8;
        if (ndx >= length)
            throw new ArrayIndexOutOfBoundsException("Index: "+ ndx +", length: "+ length);
        assert block < length : "Index of "+ ndx +" goes to block "+ block +" of array length "+ length;
        final int offset = ndx - (block * 8);
        bits[block] = set(bits[block], offset, value);
    }

    /**
     * Sets the bit at the given index into the byte to the give
     * value.
     */
    public static byte set (final byte b, final int offset, final int value) {
        if (value == 0) {
            final int mask = (1 << offset) ^ 0xFF;
            return (byte) (b & mask);
        } else {
            final int mask = 1 << offset;
            return (byte) (b | mask);
        }
    }

    /**
     * @throws ArrayIndexOutOfBoundsException.
     */
    public static String toString (final byte [] bits, final int length) {
        final int n = bits.length * 8;
        if (length < 0 || length >= n)
            throw new ArrayIndexOutOfBoundsException("Length: "+ length +" is < 0 or >= number of bits in array: "+ n);
        String s = "";
        for (int i = 0; i < n; i++)
            s = get(bits, i, length) + s;
        return s;
    }
}
