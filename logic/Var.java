package logic;

/**
 * The Var class represents a Boolean variable that has an assignable
 * truth value..
 *
 * @author Pablo Mayrgundter
 */
public class Var implements Proposition {

  public static final Var TRUE = new Var("TRUE", true);
  public static final Var FALSE = new Var("FALSE", false);

  final String name;
  Boolean val;

  public Var(String name) {
    this(name, null);
  }

  public Var(String name, Boolean val) {
    this.name = name;
    this.val = val;
  }

  public Boolean set(Boolean val) {
    final Boolean retVal = val;
    this.val = val;
    return retVal;
  }

  public boolean isBound() {
    return val != null;
  }

  public Proposition reduce() {
    if (isBound()) {
      return val ? Var.TRUE : Var.FALSE;
    }
    throw new IllegalStateException("Variable is unbound");
  }

  public boolean isTrue() {
    if (isBound()) {
      return val;
    }
    throw new IllegalStateException("Variable is unbound");
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return String.format("%s:%s", getName(), isBound() ? val.toString() : "?");
  }
}
