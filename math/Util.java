package math;

public class Util {

  public static double entropy(final double probability) {
    return -1.0 * log(2, probability);
  }

  public static double log(final double base, final double x) {
    // log_a(x) = log_a(b) * log_b(x)
    //          = 1/log_b(a) * log_b(x)
    //          = 1/log(a) * log(x)
    return 1.0 / Math.log(base) * Math.log(x);
  }

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
