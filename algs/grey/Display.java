package algs.grey;

import gfx.vt.VT100;

class Display {
  public static void testCoordFromCode(final int width) {
    for (int i = 0, n = width * width; i < n; i++) {
      final String [] coord = ZigZag.coordFromCode(width, i).split(",");
      final int x = Integer.parseInt(coord[0]);
      final int y = Integer.parseInt(coord[1]);
      System.out.print(VT100.cursorForce(y, x)+"X");
      try { Thread.sleep(42); } catch (Exception e) {}
    }
    System.out.println();
  }

  public static void main(final String [] args) {
    final int width = Integer.parseInt(args[0]);
    System.out.print(VT100.CLEAR_SCREEN);
    testCoordFromCode(width);
  }
}