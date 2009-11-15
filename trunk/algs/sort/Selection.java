package algs.sort;

/**
 * @author Pablo Mayrgundter
 * @see <a href="http://en.wikipedia.org/wiki/Selection_sort">http://en.wikipedia.org/wiki/Selection_sort</a>
 */
final class Selection {
  public static void sort(final int [] vals) {
    sort(vals, 0, vals.length);
  }
  public static void sort(final int [] vals, final int from, final int to) {
    if (to - from <= 1)
      return;
    for (int i = from; i < to - 1; i++) {
      int min = i;
      for (int j = i + 1; j < to; j++) {
        if (vals[j] < vals[min])
          min = j;
      }
      if (i != min)
        algs.Util.swap(vals, min, i);
    }
  }
}