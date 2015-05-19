package logic;

import java.util.Collection;

/**
 * The Not class represents the Boolean unary negation (NOT, ~, !)
 * operation.
 *
 * @author Pablo Mayrgundter
 */
public class Not extends Operator {

  static final long serialVersionUID = -6823975371274852602L;

  public Not() {
    super(1);
  }

  public Not(Proposition p) {
    add(p);
  }

  public boolean add(Proposition p) {
    if (size() >= 1) {
      throw new IllegalStateException("Not already has one argument.");
    }
    return super.add(p);
  }

  public boolean addAll(Collection<? extends Proposition> c) {
    throw new UnsupportedOperationException();
  } 

  public boolean isTrue() {
    return !get(0).isTrue();
  }

  public Proposition reduce() {
    Proposition arg = get(0);
    set(0, arg = arg.reduce());
    if (!arg.isBound()) {
      return this;
    }
    return arg.isTrue() ? Var.TRUE : Var.FALSE;
  }
}
