package fun;

import java.util.Random;

/**
 * Given:
 *
 *   a b c d
 *   e f g h
 *
 * Stored in row-major order as:
 *
 *   a b c d e f g h
 *
 *   0 1 2 3 4 5 6 7
 *   - - - - - - - -
 *   A b c d e f g h  
 *     E c d b f g h  1,4
 *       B d c f g h  2,4
 *         F c d g h  3,5
 *           C d g h  4,4
 *             G d h  5,6
 *               D h  6,6
 *                 H  7,7
 *
 *   A E B F C G D H
 *
 * Yields:
 *
 *   a e
 *   b f
 *   c g
 *   d h
 *
 * Stored in row-major order as:
 *
 *   a e b f c g d h
 */
class Transpose {
  int rows = 2;
  int cols = 4;
  int [] arr = new int[rows * cols];

  Transpose() {
    Random r = new Random();
    for (int i = 0; i < arr.length; i++) {
      int row = i / cols;
      int col = i % cols;
      arr[row * cols + col] = r.nextInt(10);
    }
  }

  void print() {
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i] + " ");
      if ((i + 1) % cols == 0) {
        System.out.println();
      }
    }
  }

  void transpose() {
    swap(0, 0);
    swap(1, 4);
    swap(2, 4);
    swap(3, 5);
    swap(4, 4);
    swap(5, 6);
    swap(6, 6);
    swap(7, 7);
    int t = cols;
    cols = rows;
    rows = t;
  }

  void swap(int i, int j) {
    int t = arr[i];
    arr[i] = arr[j];
    arr[j] = t;
  }

  public static void main(String [] args) {
    Transpose t = new Transpose();
    t.print();
    t.transpose();
    System.out.println();
    t.print();
  }
}
