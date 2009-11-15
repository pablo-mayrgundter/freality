package algs.sort;

/**
 * @author Pablo Mayrgundter
 * @see <a href="http://en.wikipedia.org/wiki/Merge_sort">http://en.wikipedia.org/wiki/Merge_sort</a>
 */
final class Merge {
  public static void sort(final int [] vals) {
    sort(vals, 0, vals.length);
  }
  public static void sort(final int [] vals, final int from, final int to) {
    if (to - from <= 1)
      return;
    final int mid = to - (int)Math.floor((to - from) / 2);
    sort(vals, from, mid);
    sort(vals, mid, to);
    merge(vals, from, mid, to);
  }
  static void merge(final int [] vals, final int from, final int mid, final int to) {
    assert from <= mid && mid < to : "from <= mid && mid < to";
    final int len = to - from;
    int [] tmp = new int[len];
    int x = from, y = mid;
    for (int i = 0; i < len; i++) {
      if (vals[x] <= vals[y])
        tmp[i] = vals[x++];
      else
        tmp[i] = vals[y++];
      if (x == mid) {
        System.arraycopy(vals, y, tmp, ++i, to - y);
        break;
      }
      if (y == to) {
        System.arraycopy(vals, x, tmp, ++i, mid - x);
        break;
      }
    }
    System.arraycopy(tmp, 0, vals, from, len);
  }
}
