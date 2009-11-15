package ai;

class SineEnvironment extends Environment<Double> {
  @Override
  public void set(final Double output) {
    state = 2.0 * Math.sin(output);
  }
}