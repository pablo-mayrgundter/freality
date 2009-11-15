package ai;

class SineEnvironment extends Environment {
  @Override
  public void set(final double output) {
    state = 2.0 * Math.sin(state);
  }
}