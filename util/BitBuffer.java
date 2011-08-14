package util;

import java.nio.ByteBuffer;

public final class BitBuffer {

  final ByteBuffer mBuf;
  final int mCapacity;
  int mPos, mLim;

  public BitBuffer (final int capacity) {
    mBuf = ByteBuffer.allocate(capacity / 8 + ((capacity % 8) == 0 ? 0 : 1));
    mCapacity = capacity;
    mPos = 0;
    mLim = mCapacity;
  }

  public ByteBuffer getInner () {
    return mBuf;
  }

  public int capacity () {
    return mCapacity;
  }

  public int position () {
    return mPos;
  }

  public int limit () {
    return mLim;
  }

  public int remaining () {
    return mLim - mPos;
  }

  public BitBuffer clear() {
    for (int i = mPos; i < mLim; i++) {
      set(i, 0);
    }
    return this;
  }

  public int get(final int ndx) {
    final int block = ndx / 8;
    final int offset = ndx - (block * 8);
    if (ndx < mPos || ndx >= mLim) {
      throw new ArrayIndexOutOfBoundsException("Index: "+ ndx +", "+ this);
    }
    assert block < mBuf.limit() : "Index of "+ ndx +" goes to block "+ block +" of inner byte-buf: "+ mBuf;
    return Bits.get(mBuf.get(block), offset);
  }

  public BitBuffer set(final int ndx, final int value) {
    final int block = ndx / 8;
    final int offset = ndx - (block * 8);
    if (ndx >= mLim) {
      throw new ArrayIndexOutOfBoundsException("Index: "+ ndx +", "+ this);
    }
    assert block < mBuf.limit() : "Index of "+ ndx +" goes to block "+ block +" of inner byte-buf"+ mBuf;
    final byte withBitSet = Bits.set(mBuf.get(block), offset, value);
    mBuf.put(block, withBitSet);
    return this;
  }

  public String toString () {
    return String.format("pos: %d, lim: %d, capacity: %d", mPos, mLim, mCapacity);
  }
}
