package logic;

/**
 * The False class represents a proposition that is always false.
 *
 * @author Pablo Mayrgundter
 */
public class False extends Proposition {
  public boolean isTrue() { return false; }
  public String toString() { return "FALSE"; }
}
