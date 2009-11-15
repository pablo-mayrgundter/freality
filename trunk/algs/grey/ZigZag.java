package algs.grey;

/**
 * The ZigZag class provides methods for getting a zigzag code from a
 * coordinate, or a coordinate given a code, both in constant time and
 * space.
 *
 * A zigzag code is a space-filling curve through a square grid of
 * coordinates, starting at S and finishing at F:
 *
 * S 0-0 0-0
 * |/ / / /
 * 0 0 0 0 0
 *  / / / /|
 * 0 0 0 0 0
 * |/ / / /
 * 0 0 0 0 0
 *  / / / /|
 * 0-0 0-0 F
 *
 * The following is a table showing code/coord association:
 *
 * I: x,y
 * 0: 0,0
 * 1: 0,1
 * 2: 1,0
 * 3: 2,0
 * 4: 1,1
 * 5: 0,2
 * 6: 0,3
 * 7: 1,2
 * 8: 2,1
 * 9: 3,0
 *
 * Note, both methods use what seems to be an overly verbose method
 * of calculating overcount difference between the zigzag code in a
 * square compare to its code in the circumscribed triangle.
 *
 * @author Pablo Mayrgundter
 */
class ZigZag {

  /** @return The zigzag code for the given point x,y within a square grid of the given width. */
  static int codeFromCoord(final int width, final int x, final int y) {
    final int level = x + y;
    final int parity = level % 2;
    final int levelCode = (level * (level + 1)) / 2; // Summation(level)
    int code = levelCode + (parity == 0 ? y : x);
    // Handle overcount.
    if (level >= width) {
      final int foo = level - width + 1;
      final int overcount = foo * foo;
      code -= overcount;
    }
    return code;
  }

  /** @return The coordinate for a given zigzag code within a sqare of the given width. */
  static String coordFromCode(final int width, int code) {
    // Handle overcount first.
    if (code >= 0.5 * width * (width + 1)) {
      final int widthSquared = width * width;
      final int descCode = widthSquared - 1 - code;
      final int levelRight = (int)(Math.sqrt(0.25 + 2.0 * descCode) - 0.5);
      final int foo = levelRight - width + 1;
      final int overcount = foo * foo;
      code += overcount;
    }
    final int level = (int)(Math.sqrt(0.25 + 2.0 * code) - 0.5);
    final int parity = level % 2;
    final int base = (level * (level + 1)) / 2; // Summation(level)
    final int d = code - base;
    final int x = parity == 0 ? level - d : d;
    final int y = parity == 0 ? d : level - d;
    return x+","+y;
  }

  public static void testCodeFromCoord(final int width) {
    for (int y = 0; y < width; y++)
      for (int x = 0; x < width; x++)
        System.out.println(x +","+ y +": "+ codeFromCoord(width, x, y));
  }

  public static void testCoordFromCode(final int width) {
    for (int i = 0, n = width * width; i < n; i++) {
      final String [] coord = coordFromCode(width, i).split(",");
      final int x = Integer.parseInt(coord[0]);
      final int y = Integer.parseInt(coord[1]);
      assert codeFromCoord(width, x, y) == i;
      System.out.printf("%d: %d,%d\n", i, x, y);
    }
  }

  public static void main(final String [] args) {
    final int width = Integer.parseInt(args[0]);
    testCodeFromCoord(width);
    testCoordFromCode(width);
  }
}