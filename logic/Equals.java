package logic;

import java.util.Arrays;
import java.util.Collection;

/**
 * The Equals class represents Boolean equality (XNOR, =) operation.
 *
 * @author Pablo Mayrgundter
 */
class Equals extends Operator {

  public Equals(Proposition ... args) {
    this(Arrays.asList(args));
  }

  public Equals() {
  }

  public Equals(Collection<? extends Proposition> c) {
    super(c);
  }

  public Equals(int initialCapacity) {
    super(initialCapacity);
  }

  public boolean isTrue() {
    boolean first = get(0).isTrue();
    boolean t = true;
    for (int i = 1; i < size(); i++)
      t &= first == get(i).isTrue();
    return t;
  }

  public Proposition reduce() {
    throw new UnsupportedOperationException();
  }

  public String toString() {
    final StringBuffer buf = new StringBuffer();
    for (int i = 0; i < size(); i++) {
      if (i > 0)
        buf.append(" == ");
      buf.append(get(i));
    }
    return buf.toString();
  }
}
