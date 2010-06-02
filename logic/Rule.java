package logic;

/**
 * A Rule is a sequential proposition of the form x ? y : z.  That is,
 * if x is true, then the expression is true iff y is true.  Otherwise
 * the expression is true iff z is true.
 *
 * This class currently subclasses Conjunction, which is probably
 * incorrect.  This may soon change.
 */
public class Rule extends Conjunction {

  final Proposition _negImp;

  public Rule(Proposition condition, Proposition implication, Proposition negativeImplication) {
    super(new Proposition[]{condition, implication, negativeImplication});
    _negImp = negativeImplication;
  }

  public synchronized Proposition evaluate(Object variable) {
    setVariable(variable);
    if (_propositions[0].isTrue()) return _propositions[1];
    return _negImp;
  }

  public Proposition getCondition() { return _propositions[0]; }
  public Proposition getImplication() { return _propositions[1]; }
  public Proposition getNegativeImplication() { return _negImp; }

  public String toString() {
    return "IF " + _propositions[0] + " THEN " + _propositions[1] + " ELSE " + _negImp;
  }
}
