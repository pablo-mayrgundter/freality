package logic;

/**
 * A Conjunction is true iff all of its sub-statements are true.
 *
 * @author Pablo Mayrgundter
 */
public class Conjunction extends Operator {

  public Conjunction(Proposition p1, Proposition p2) {
    this(new Proposition[]{p1, p2});
  }

  public Conjunction(Proposition [] propositions) {
    super(propositions);
  }

  public boolean isTrue() {
    boolean t = true;
    for (int i = 0; i < _propositions.length; i++)
      t &= _propositions[i].isTrue();
    return t;
  }

  public String toString() {
    final StringBuffer buf = new StringBuffer();
    for (int i = 0; i < _propositions.length; i++) {
      if (i > 0)
        buf.append(" AND ");
      buf.append(_propositions[i]);
    }
    return buf.toString();
  }
}
