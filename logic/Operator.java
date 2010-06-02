package logic;

/**
 * An Operator has a truth value that is conditioned on the truth
 * values of its sub-statements.
 *
 * @author Pablo Mayrgundter
 */
public abstract class Operator extends Predicate {

  protected final Proposition [] _propositions;

  Operator(Proposition [] propositions) {
    _propositions = propositions;
  }

  public int numPropositions() { return _propositions.length; }

  public Proposition getProposition(int index) { return _propositions[index]; }
}
    
