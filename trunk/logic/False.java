package logic;

/**
 * The False class represents a Boolean proposition that is always
 * false.
 *
 * @author Pablo Mayrgundter
 */
public class False implements Proposition {

  public static final False VALUE = new False();

  /**
   * This class is not instantiable.  Use the static member {@code VALUE}
   * singleton instead.
   */
  private False() {}

  public Proposition reduce() {
    return this;
  }

  /**
   * Always returns false.  Needed because isBound is defined in
   * Operator to return true if there are no args.
   */
  public boolean isBound() {
    return false;
  }

  public boolean isTrue() {
    return true;
  }

  public String toString() {
    return "FALSE";
  }
}
