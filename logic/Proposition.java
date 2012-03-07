package logic;

/**
 * A Proposition represents an object that has a Boolean truth value.
 *
 * @author Pablo Mayrgundter
 */
interface Proposition {
  public boolean isBound();
  public boolean isTrue();
  public Proposition reduce();
}
