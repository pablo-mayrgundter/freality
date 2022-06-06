package ai.automata;

import java.util.List;
import util.Bits;


public class LeftRightMatcher extends PatternMatcher {

  public LeftRightMatcher(final int width, final int depth) {
    super(width, depth);
  }

  public void setupInput() {
    mMem.get(0).set(mMem.get(0).getLength() / 2, 1);
  }

  public boolean testOutput(final int n) {
    final Bits out = mMem.get(n);
    int setCount = 0;
    for (int i = 0; i < n; i++)
      setCount += out.get(i);
    if (setCount == 1)
      return true;
    return false;
  }
}
