package algs.sort;

/**
 * @author Pablo Mayrgundter
 * @see <a href="http://en.wikipedia.org/wiki/Insertion_sort">http://en.wikipedia.org/wiki/Insertion_sort</a>
 */
final class Insertion {
  public static void sort(final int [] vals) {
    sort(vals, 0, vals.length);
  }
  public static void sort(final int [] vals, final int from, final int to) {
    if (to - from <= 1)
      return;
    for (int j = from; j < to; j++) {
      final int x = vals[j];
      for (int i = from; i < j; i++) {
        final int y = vals[i];
        if (x < y) {
          algs.Util.swap(vals, i, j);
        }
      }
    }
  }
}
