package ai;

class SinglePeakEnvironment extends Environment {
  @Override
  public void set(final double val) {
    state = 1.0 - Math.abs(val);
  }
}
