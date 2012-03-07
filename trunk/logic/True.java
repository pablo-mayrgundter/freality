package logic;

/**
 * The True class represents a Boolean proposition that is always
 * true.
 *
 * @author Pablo Mayrgundter
 */
public class True implements Proposition {

  public static final True VALUE = new True();

  /**
   * This class is not instantiable.  Use the static member {@code VALUE}
   * singleton instead.
   */
  private True() {}

  public Proposition reduce() {
    return this;
  }

  /**
   * Always returns true.
   */
  public boolean isBound() {
    return true;
  }

  public boolean isTrue() {
    return true;
  }

  public String toString() {
    return "TRUE";
  }
}
