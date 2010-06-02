package logic;

/**
 * The True class represents a proposition that is always true.
 *
 * @author Pablo Mayrgundter
 */
public class True extends Proposition {
  public boolean isTrue() { return true; }
  public String toString() { return "TRUE"; }
}
