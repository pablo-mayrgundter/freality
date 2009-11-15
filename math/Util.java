package math;

class Util {

  static void fib(long x, long y, int step, int iterations) {
    if (x > x + y || step + 1 >= iterations)
      return;
    System.err.println(x);
    fib(y, x + y, step + 1, iterations);
  }

  public static void main(final String [] args) {
    if (args.length < 1) {
      System.err.println("Usage: java math.Util <fib [iterations]>");
      return;
    }
    if (args[0].equals("fib")) {
      int iterations = 100;
      if (args.length > 1)
        iterations = Integer.parseInt(args[1]);
      fib(0, 1, 0, iterations);
    }
  }
}
