package logic;

/**
 * A predicate is a proposition that is true conditioned on a
 * variable.  The propositional truth can be tested only after setting
 * the variable.
 *
 * @author Pablo Mayrgundter
 */
public abstract class Predicate extends Proposition implements HasVariable {

  transient protected Object _variable;

  public Object getVariable() {
    return _variable;
  }

  /**
   * NOTE: This method is not thread-safe with isTrue().  For
   * thread-safe sequential evaluation, use evaluate(Object).
   */
  public void setVariable(Object variable) {
    _variable = variable;
  }

  public Proposition evaluate(Object variable) {
    setVariable(variable);
    return isTrue() ? ((Proposition) new True()) : ((Proposition) new False());
  }

  public String toString() { return "PREDICATE(X)"; }
}
