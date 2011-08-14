package phys;

import java.awt.Color;

final class ColoredBlob extends Blob {

  static final String COLORS_CSV = System.getProperty("colors", "black");
  static final Color [] COLORS;
  static {
    String [] colors = COLORS_CSV.split(",");
    COLORS = new Color[colors.length];
    for (int i = 0; i < colors.length; i++) {
      try {
      java.lang.reflect.Field f = Color.class.getDeclaredField(colors[i].toUpperCase());
      COLORS[i] = (Color)f.get(Color.class);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  Color color;
  void setMass(final float mass) {
    this.mass = mass;
    final int ndx = (int)(mass / (MAXMASS * 1.1f) * (float)COLORS.length);
    color = COLORS[ndx];
    radius = (int) (mass * SIZE);
  }
}