package ai;

public class Environment<T> {
  protected T state;
  public T get() {
    return state;
  }
  public void set(final T val) {
    state = val;
  }
}