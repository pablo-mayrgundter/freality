package math.util;

public final class Linear {
  /**
   * Squeeze b between and c.
   * @return max(a, min(b, c)
   */
  public static int squeezeToInt(final double a, final double b, final double c) {
    return (int) Math.max(a, Math.min(b, c));
  }

  /**
   * Squeeze b between and c.
   * @return max(a, min(b, c)
   */
  public static double squeeze(final double a, final double b, final double c) {
    return Math.max(a, Math.min(b, c));
  }
}
