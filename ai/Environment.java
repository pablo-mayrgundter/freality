package ai;

class Environment {
  double state = 0;
  double get() {
    return state;
  }
  void set(final double val) {
    state = val;
  }
}