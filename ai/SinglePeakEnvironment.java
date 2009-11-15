package ai;

class SinglePeakEnvironment extends Environment<Double> {
  @Override
  public void set(final Double val) {
    state = 1.0 - Math.abs(val);
  }
}
