package ai.spatial;

import ai.Environment;

public class SineEnvironment extends Environment<Double> {
  public SineEnvironment() {
  }
  @Override
  public void set(final Double output) {
    state = 2.0 * Math.sin(state);
  }
}
