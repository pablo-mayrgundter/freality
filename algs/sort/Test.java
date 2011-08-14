package algs.sort;

import java.util.Arrays;

/**
 * Usage: algs.sort.Test [-help] [-Dsort=java.util.Arrays|algs.sort.*] [-Dlength=10|n] [-Diterations=1|n] [-Dvalues=rand()|x,y,z,...]
 *
 * @author Pablo Mayrgundter
 */
class Test {

  static final String usage = "Usage: algs.sort.Test [-help] [-Dsort=java.util.Arrays|algs.sort.*] [-Dlength=10|n] [-Diterations=1|n] [-Dvalues=rand()|x,y,z,...]";
  static final boolean help = Boolean.getBoolean("help");
  static final String sort = System.getProperty("sort", "java.util.Arrays");
  static final int length = Integer.parseInt(System.getProperty("length", "10"));
  static final int iterations = Integer.parseInt(System.getProperty("iterations", "1"));
  static final String values = System.getProperty("values", "rand");

  @SuppressWarnings(value="unchecked")
  public static void main(final String [] args) {
    if (help) {
      System.err.println(usage);
      return;
    }
    int [] vals = new int[length];
    if (values.equals("rand")) {
      for (int i = 0; i < vals.length; i++)
        vals[i] = (int)(100.0 * Math.random());
    } else {
      String [] valStrs = values.split(",");
      vals = new int[valStrs.length];
      for (int i = 0; i < vals.length; i++)
        vals[i] = Integer.parseInt(valStrs[i]);
    }

    assert algs.Util.p(Arrays.toString(vals));

    final int [] copy = new int[vals.length];
    try {
      final Class sortClass = Class.forName(sort);
      final java.lang.reflect.Method sortMethod = sortClass.getDeclaredMethod("sort", new int[]{}.getClass());
      long time = System.currentTimeMillis();
      for (int i = 0; i < iterations; i++) {
        System.arraycopy(vals, 0, copy, 0, vals.length);
        sortMethod.invoke(null, copy);
      }
      time = System.currentTimeMillis() - time;
      if (iterations > 1) {
        algs.Util.p("elapsed: "+ time + "ms");
      }
    } catch (final Exception e) {
      System.err.println(e);
      return;
    }

    assert algs.Util.p(java.util.Arrays.toString(copy));
    int cur = copy[0];
    boolean sorted = true;
    for (int x : copy) {
      if (cur > x) {
        sorted = false;
        break;
      }
      cur = x;
    }
    if (!sorted) {
      algs.Util.p("Not sorted!");
    } else {
      assert algs.Util.p("Sorted!");
    }
  }
}
