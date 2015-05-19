package logic;

import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;

/**
 * The Or class represents the Boolean binary disjunction (OR, |)
 * operation.
 *
 * @author Pablo Mayrgundter
 */
public class Or extends Operator {

  static final long serialVersionUID = -2758062749416762167L;

  public Or() {
  }

  public Or(Proposition ... args) {
    this(Arrays.asList(args));
  }

  public Or(Collection<? extends Proposition> c) {
    super(c);
  }

  public Or(int initialCapacity) {
    super(initialCapacity);
  }

  public boolean isTrue() {
    for (Proposition p : this) {
      if (p.isTrue()) {
        return true;
      }
    }
    return false;
  }

  public Proposition reduce() {
    boolean t = false;
    ListIterator<Proposition> argItr = this.listIterator();
    while (argItr.hasNext()) {
      Proposition arg = argItr.next().reduce();
      if (arg.isBound()) {
        if (arg.isTrue()) {
          return Var.TRUE;
        }
        argItr.remove();
      } else {
        argItr.set(arg);
      }
    }
    if (!isEmpty()) {
      return this;
    }
    return t ? Var.TRUE : Var.FALSE;
  }
}
