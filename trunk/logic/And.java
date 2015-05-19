package logic;

import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;

/**
 * The And class represents the Boolean binary conjunction (AND,
 * &amp;) operation.
 *
 * @author Pablo Mayrgundter
 */
public class And extends Operator {

  static final long serialVersionUID = 7934299397464692116L;

  public And() {
  }

  public And(Proposition ... args) {
    this(Arrays.asList(args));
  }

  public And(Collection<? extends Proposition> c) {
    super(c);
  }

  public And(int initialCapacity) {
    super(initialCapacity);
  }

  public boolean isTrue() {
    for (Proposition p : this) {
      if (!p.isTrue()) {
        return false;
      }
    }
    return true;
  }

  public Proposition reduce() {
    boolean t = false;
    ListIterator<Proposition> argItr = this.listIterator();
    while (argItr.hasNext()) {
      Proposition arg = argItr.next().reduce();
      if (arg.isBound()) {
        if (!arg.isTrue()) {
          return Var.FALSE;
        }
        argItr.remove();
        t = true;
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
