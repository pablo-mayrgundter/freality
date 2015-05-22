package logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An Operator has a truth value that is conditioned on the truth
 * values of its arguments.
 *
 * @author Pablo Mayrgundter
 */
public abstract class Operator
  extends ArrayList<Proposition>
  implements Proposition, List<Proposition> {

  /**
   * Equivalent to Operator(2);
   */
  public Operator() {
    super(2);
  }

  public Operator(Collection<? extends Proposition> c) {
    super(c);
  }

  public Operator(int initialCapacity) {
    super(initialCapacity);
  }

  public boolean isBound() {
    for (Proposition p : this) {
      if (!p.isBound()) {
        return false;
      }
    }
    return true;
  }

  public String getName() {
    return this.getClass().getSimpleName().toUpperCase();
  }

  public String toString() {
    String opStr = get(0).toString();
    for (int i = 1; i < size(); i++) {
      opStr += String.format(" %s %s", getName(), get(i));
    }
    return opStr;
  }
}
    
