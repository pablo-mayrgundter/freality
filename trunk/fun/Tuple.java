package fun;

class Tuple {

  final Object [] args;

  Tuple(Object ... args) {
    this.args = args;
  }

  String [] keys() {
    // TODO(pablo):
    return new String[]{};
  }

  public String toString() {
    return String.format("Tuple@%s{%s}",
                         System.identityHashCode(this), args);
  }
}