package vr.cpack.space.model;

public class Color {

  static final float MIN_BLUE_INDEX = -0.4f;
  static final float WHITE_INDEX = 0.2f;
  static final float MAX_RED_INDEX = 5.46f;

  public final float red;
  public final float green;
  public final float blue;

  public Color(float red, float green, float blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /**
   * Computes the BV color index.
   */
  public float toColorIndex() {
    return blue - ((red + green) / MAX_RED_INDEX);
  }

  /**
   * BV color is the blue (or photographic) magnitude - visual
   * magnitude.  For objects emitting light in visible wavelengths,
   * a single color index can be used, as green doesn't seem to be
   * emitted singularly.  So, given a BV index, a guess can be made
   * as to the red and blue components, which are used to generate
   * the returned color.
   */
  public static Color fromBVColorIndex(float index) {
    if (index <= WHITE_INDEX) {
      index = Math.min(Math.max(index, MIN_BLUE_INDEX), WHITE_INDEX); // Clamp to [-0.4, 0.2].
      final float delta = WHITE_INDEX - index; // [0,0.6].
      // Set blue max, and decrease R&G inversely to strength of
      // blue delta strength.  Doesn't vary whole range of blue,
      // but that's OK, as "bluest" observed stars are "pale
      // blue".
      return new Color(1f - delta, 1f - delta, 1f);
    } else {
      index = Math.min(Math.max(index, WHITE_INDEX), MAX_RED_INDEX); // Clamp to [0.2, 2.0].
      final float delta = MAX_RED_INDEX - index; // [0, 1.8].
      // Reduce the green a bit to give good reds (?).
      return new Color(1f, Math.min(delta * 0.5f, 1f), Math.min(delta * 0.25f, 1f));
    }
  }

  public String toString() {
    return "r,g,b: " + red + "," + green + "," + blue;
  }

  public static void main(String [] args) {
    System.out.println(fromBVColorIndex(Float.parseFloat(args[0])));
  }
}
