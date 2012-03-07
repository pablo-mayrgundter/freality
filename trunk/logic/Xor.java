package logic;

import java.util.Arrays;
import java.util.Collection;

class Xor extends Operator {

  public Xor() {
  }

  public Xor(Proposition ... args) {
    this(Arrays.asList(args));
  }

  public Xor(Collection<? extends Proposition> c) {
    super(c);
  }

  public Xor(int initialCapacity) {
    super(initialCapacity);
  }

  public boolean isTrue() {
    throw new UnsupportedOperationException();
  }

  public Proposition reduce() {
    throw new UnsupportedOperationException();
  }
}
