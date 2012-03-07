package fun;

/**
 * From http://en.wikipedia.org/wiki/Resonance
 *
 *   f=Nv/2d
 *
 * @author Pablo Mayrgundter
 */
public class Resonance {

  public static void main(String [] args) {
    final int N = 1;
    final double velocity = Double.parseDouble(args[0]);
    final double distance = Double.parseDouble(args[1]);
    double frequency = (((double)N) * velocity) / (2.0 * distance);
    System.out.printf("velocity: %f meters per second\ndistance: %f meters\npredicted resonant frequency: %f cycles per second\n", velocity, distance, frequency);
  }
}