package logic;

/**
 * A Disjunction is true iff any of its sub-statements is true.
 *
 * @author Pablo Mayrgundter
 */
public class Disjunction extends Operator {

  public Disjunction(Proposition p1, Proposition p2) {
    super(new Proposition[]{p1, p2});
  }

  public Disjunction(Proposition [] propositions) {
    super(propositions);
  }

  public boolean isTrue() {
    boolean t = false;
    for (int i = 0; i < _propositions.length; i++)
      t |= _propositions[i].isTrue();
    return t;
  }

  public String toString() {
    final StringBuffer buf = new StringBuffer();
    for (int i = 0; i < _propositions.length; i++) {
      if (i > 0)
        buf.append(" OR ");
      buf.append(_propositions[i]);
    }
    return buf.toString();
  }
}
