package bench.math;

public class Test {
  public static void main(final String [] args) {
    final int numIts = Integer.parseInt(args[0]);
    int i;
    for (i = 0; i < numIts; i++)
      Math.sin(i);
  }
}