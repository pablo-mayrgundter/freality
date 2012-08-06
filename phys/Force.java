package phys;

// TODO(pablo): clearly need to generalize to multi-dimensional space.
public abstract class Force {

  public void apply(Space2D before, Space2D after) {
    int radius = before.getRadius();
    for (int x = 0; x < radius; x++) { 
      for (int y = 0; y < radius; y++) {
        apply(radius, before, after, x, y);
      }
    }
  }

  public abstract void apply(int radius, Space2D before, Space2D after,
                             int x, int y);
}