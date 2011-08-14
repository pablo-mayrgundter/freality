package al.gene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

@SuppressWarnings(value="serial")
public class Pool extends ArrayList<Gene> {

  static final Random R = new Random();

  void generation() {
    for (int i = 0; i < size(); i++) {
      Gene g = get(i);
      if (g.fitness > 0) {
        g = g.cross(get(R.nextInt(size())));
        for (int n = 0; n < g.fitness; n++)
          set(R.nextInt(size()), g);
      }
      if (g.fitness < 0)
        g = g.mutate();
    }
  }
}