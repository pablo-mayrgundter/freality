package ai.automata;

import math.automata.Wolfram;
import util.Bits;
import java.util.ArrayList;
import java.util.List;

public abstract class PatternMatcher {

  public final List<Bits> mMem;
  final int mWidth;
  final List<byte []> mOps;
  int mCapacity;
  int mDepth;

  PatternMatcher(final int width, final int depth) {
    mMem = new ArrayList<Bits>();
    mOps = new ArrayList<byte []>();
    mWidth = width;
    mCapacity = mDepth = depth;
    // Always need one more mem row than ops rows.
    for (int i = 0; i < depth + 1; i++) {
      mMem.add(new Bits(mWidth));
      grow();
    }
  }

  public List<Bits> getMemory() {
    return mMem;
  }

  public byte getRule(int row, int col) {
    return mOps.get(row)[col];
  }

  public void setRule(int row, int col, byte rule) {
    mOps.get(row)[col] = rule;
  }

  public abstract void setupInput();

  public abstract boolean testOutput(final int outRow);

  void grow() {
    mMem.add(new Bits(mWidth));
    mOps.add(new byte[mWidth]);
    mCapacity++;
  }

  public void run() {

    // Feed forwards.
    for (int row = 0; row < mDepth; row++) {

      // Apply current ops to cur mem row, output to next mem row.
      final Bits src = mMem.get(row), dst = mMem.get(row + 1);
      for (int col = 1, len = src.getLength() - 1; col < len; col++) {
        Wolfram.apply(src, col, dst, col, mOps.get(row)[col]);
      }

      // Wrap edges.
      dst.set(0, dst.get(1));
      dst.set(dst.getLength() - 1, dst.get(1));
    }
  }
}
