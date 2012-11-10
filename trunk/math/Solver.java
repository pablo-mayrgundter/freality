package math;

public class Solver extends Explorer {

  Function a = new Function() {
      public void doOp(Point p) {
        p.x = p.x * 3 + 2;
      }
    };

  Solver() {
  }

  public void run() {
    Point p = new Point();
    for (int i = 0; i < 10; i++) {
      a.doOp(p);
      System.out.printf("%s\n", p);
    }
  }

  public static void main(final String [] args) {
    new Solver().run();
  }
}
