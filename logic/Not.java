package logic;

/**
 * The Not operator inverts the truth value of is sub-propositino.
 *
 * @author Pablo Mayrgundter
 */
public class Not extends Operator {

  public Not(Proposition proposition) {
    super(new Proposition[]{proposition});
  }

  public boolean isTrue() { return ! _propositions[0].isTrue(); }

  public String toString() { return "NOT " + _propositions[0]; }
}
