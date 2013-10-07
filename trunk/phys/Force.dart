part of phys;

// TODO(pablo): clearly need to generalize to multi-dimensional space.
abstract class Force {

  void apply(Space2D before, Space2D after) {
    int radius = before.getRadius();
    for (int x = 1; x < radius - 1; x++) { 
      for (int y = 1; y < radius - 1; y++) {
        applyWithin(radius, before, after, x, y);
      }
    }
  }

  void applyWithin(int radius, Space2D before, Space2D after,
                   int x, int y) {}
}