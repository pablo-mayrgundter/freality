package gfx;

import java.awt.Color;

public class ColorMap {
  public static Color[] linear(final int levels) {
    return linear(1, 1, 1, levels);
  }

  public static Color[] linear(final Color c, final int levels) {
    return linear(c == Color.RED ? 1 : 0,
                  c == Color.GREEN ? 1 : 0,
                  c == Color.BLUE ? 1 : 0,
                  levels);
  }

  public static Color[] linear(final float r, final float g, final float b, final int levels) {
    final Color [] colors = new Color[levels];
    for (int i = 0; i < colors.length; i++) {
      final float level = (float)i/(float)colors.length;
      colors[i] = new Color(r * level, g * level, b * level);
    }
    return colors;
  }
}