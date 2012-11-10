package math.complexity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import math.Function;
import math.Point;
import math.util.Xml;
import util.Flags;

public class ChaosGame implements Runnable {

  static Flags flags = new Flags(ChaosGame.class);
  static int FUNC_NDX = flags.get("func_ndx", "func_ndx", -1);
  static int NUM_FUNCS = flags.get("num_funcs", "num_funcs", 15);
  static boolean RAND_COOEF = flags.get("rand_coeff", "rand_coeff", true);
  static double DT = flags.get("dt", "dt", 1.0);

  interface Flame extends Function {
    void rotate(double theta);
    String toXml();
    void randCooef(Random r);
    int getId();
  }

  static class Linear implements Flame {

    static int SERIAL = 0;

    double weight, color, a, b, c, d, e, f, dt = DT;
    int id;

    Linear() {
      this(SERIAL++);
    }

    Linear(int id) {
      this.id = id;
    }

    public Linear(final org.w3c.dom.Node node) {
      weight = Double.parseDouble(node.getAttributes().getNamedItem("weight").getTextContent());
      color = Integer.parseInt(node.getAttributes().getNamedItem("color").getTextContent());
      final String [] coeffs = node.getAttributes().getNamedItem("coeffs").getTextContent().split("\\s");
      a = Float.parseFloat(coeffs[0]);
      b = Float.parseFloat(coeffs[1]);
      c = Float.parseFloat(coeffs[2]);
      d = Float.parseFloat(coeffs[3]);
      e = Float.parseFloat(coeffs[4]);
      f = Float.parseFloat(coeffs[5]);
    }

    public void randCooef(Random r) {
      a = r.nextDouble() - 0.5;
      b = r.nextDouble() - 0.5;
      c = r.nextDouble() - 0.5;
      d = r.nextDouble() - 0.5;
      e = r.nextDouble() - 0.5;
      f = r.nextDouble() - 0.5;
      color = r.nextDouble();
      weight = r.nextDouble();
    }

    public int getId() {
      return id;
    }

    static Point sharedPoint = new Point(0,0,0);
    static double sharedRadius;
    static double sharedTheta;

    static void setPoint(Point point) {
      sharedPoint.x = point.x;
      sharedPoint.y = point.y;
      sharedPoint.z = point.z;
    }

    static void updateRadius() {
      sharedRadius = distance(sharedPoint);
    }

    static void updateTheta() {
      sharedTheta = theta(sharedPoint);
    }

    public void rotate(double theta) {
      a = a * Math.cos(theta) - b * Math.sin(theta);
      b = a * Math.sin(theta) + b * Math.cos(theta);

      d = d * Math.cos(theta) - e * Math.sin(theta);
      e = d * Math.sin(theta) + e * Math.cos(theta);
    }

    public String toXml() {
      String s = "";
      s += String.format("<xform weight=\"%7f\" color=\"%f\" %s=\"1\"", weight, color, equationType());
      s += String.format(" coeffs=\"%7f %7f %7f %7f %7f %7f\"/>", a, b, c, d, e, f);
      return s;
    }

    /**
     * Other types override this.
     */
    public void variation(final Point p) {
      p.x = dt * a * p.x + b * p.y + c;
      p.y = dt * d * p.x + e * p.y + f;
    }

    /**
     * Used to perform the composition of the base function's linear
     * equation as well as delegating to the instance's variation.
     */
    public void doOp(final Point p) {
      variation(p);
    }

    static double distance(final Point p) {
      return Math.sqrt(distanceSquared(p));
    }

    static double distanceSquared(final Point p) {
      final double x = p.x;
      final double y = p.y;
      return x*x + y*y;
    }

    static double theta(final Point p) {
      if (p.x == 0) {
        return 0;
      }
      return Math.atan(p.y/p.x);
    }

    String equationType() {
      String name = this.getClass().getName();
      if (name.indexOf("$") != -1) {
        name = name.split("\\$")[1];
      }
      return name;
    }
  }

  static final class Identity extends Linear {
    Identity(int id) {
      super(id);
    }
    public void variation(final Point p) {
      p.x = p.x;
      p.y = p.x;
    }
    public String toString() {
      return "x, y";
    }
  }

  static final class Parabolic extends Linear {
    Parabolic(int id) {
      super(id);
    }
    public void variation(final Point p) {
      p.x = dt * a * Math.pow(p.y, 2);
      p.y = dt * b * Math.pow(p.x, 2);
    }
    public void rotate(double theta) {
      a = a * Math.cos(theta) - b * Math.sin(theta);
      b = a * Math.sin(theta) + b * Math.cos(theta);
    }
    public String toString() {
      return "y^2, x^2";
    }
  }

  static final class Sinusoidal extends Linear {
    Sinusoidal(int id) {
      super(id);
    }
    public void variation(final Point p) {
      p.x = dt * a * Math.sin(p.x);
      p.y = dt * b * Math.sin(p.y);
    }
    public String toString() {
      return "sin(x), sin(y)";
    }
  }

  static final class Spherical extends Linear {
    Spherical(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double distanceSquared = distanceSquared(p);
      if (distanceSquared == 0) {
        distanceSquared = 0.01;
      }
      p.x = dt * (p.x / distanceSquared);
      p.y = dt * (p.y / distanceSquared);
    }
    public String toString() {
      return "(x)/r^2, (y)/r^2";
    }
  }

  static final class Swirl extends Linear {
    Swirl(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * r * Math.cos(t + r);
      p.y = dt * r * Math.sin(t + r);
    }
    public String toString() {
      return "r*cos(θ+r), r*sin(θ+r)";
    }
  }

  static final class Horseshoe extends Linear {
    Horseshoe(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * r * Math.cos(2f * t);
      p.y = dt * r * Math.sin(2f * t);
    }
    public String toString() {
      return "r*cos(2*θ), r*sin(2*θ)";
    }
  }

  static final class Polar extends Linear {
    Polar(int id) {
      super(id);
    }
    public void variation(final Point p) {
      p.x = dt * theta(p) / Math.PI;
      p.y = dt * distance(p) - 1f;
    }
    public String toString() {
      return "θ/π, r-1";
    }
  }

  static final class Hankerchief extends Linear {
    Hankerchief(int id) {
      super(id);
    }
    public void variation(final Point p) {
      final double r = distance(p);
      final double t = theta(p);
      p.x = dt * r * Math.sin(t + r);
      p.y = dt * r * Math.cos(t - r);
    }
    public String toString() {
      return "r*sin(θ+r), r*cos(θ-r)";
    }
  }

  static final class Heart extends Linear {
    Heart(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * r * Math.sin(t * r);
      p.y = dt * -r * Math.cos(t * r);
    }
    public String toString() {
      return "r*sin(θ*r), -r*cos(θ*r)";
    }
  }

  static final class Disc extends Linear {
    Disc(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * t * Math.sin(Math.PI * r) / Math.PI;
      p.y = dt * t * Math.cos(Math.PI * r) / Math.PI;
    }
    public String toString() {
      return "θ*sin(π/r)/π, θ*cos(πr)/π";
    }
  }

  static final class Spiral extends Linear {
    Spiral(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * (Math.cos(t) + Math.sin(r)) / r;
      p.y = dt * (Math.sin(t) - Math.cos(r)) / r;
    }
    public String toString() {
      return "(cos(θ) + sin(r))/r, (sin(θ) - cos(r))/r";
    }
  }

  static final class Hyperbolic extends Linear {
    Hyperbolic(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * Math.sin(t) / r;
      p.y = dt * Math.cos(t) * r;
    }
    public String toString() {
      return "sin(θ)/r, cos(θ)*r";
    }
  }

  static final class Diamond extends Linear {
    Diamond(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * Math.sin(t) / Math.cos(r);
      p.y = dt * Math.cos(t) / Math.sin(r);
    }
    public String toString() {
      return "sin(θ)/cos(r), cos(θ)/sin(r)";
    }
  }

  static final class Ex extends Linear {
    Ex(int id) {
      super(id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = dt * r * Math.pow(Math.sin(t + r), 3);
      p.y = dt * r * Math.pow(Math.cos(t - r), 3);
    }
    public String toString() {
      return "sin(θ+r)^3, cos(θ-r)^3";
    }
  }

  static final class Julia extends Linear {
    Julia (int id) {
      super(id);
      a = b = 0.1;
    }
    public void variation(final Point p) {
      final double sqr = Math.sqrt(sharedRadius) * a;
      final double t = sharedTheta * b;
      // TODO(pablo): somehow pass in seeded random.
      final double O = Math.random() <= 0.5 ? 0f : Math.PI;
      p.x = dt * sqr * Math.cos(t / 2f + O);
      p.y = dt * sqr * Math.sin(t / 2f + O);
    }
    public String toString() {
      return "sqr*cos(θ/2+Ω), sqr*sin(θ/2+Ω); Ω=0|π";
    }
  }

  static final class LorenzAttractor extends Linear {
    LorenzAttractor(int id) {
      super(id);
      // Hoist these coefficients into class members instead of
      // leaving as constants below, so that the super-class class to
      // rotate will slowly morph them.
      a = 10;
      b = 28;
      c = -2.6;
    }
    public void variation(final Point p) {
      p.x += dt * (a * (p.y - p.x));
      p.y += dt * (b * p.x - p.y - p.x * p.z);
      p.z += dt * (c * p.z + p.x * p.y);
    }
    public String toString() {
      return "d[x,y,z]/dt=[10(y-x),28x-y-xz,8/3z+xy]";
    }
  }

  // TODO(pablo): Broken..
  static final class LogisticMap extends Linear {
    final double r;
    LogisticMap(double r, int id) {
      super(id);
      this.r = r;
    }
    public void variation(final Point p) {
      //      System.out.println("in: "+ p);
      double x = p.y;
      double y = r * p.x - r * Math.pow(p.x, 2);
      p.x = dt * y;
      p.y = dt * y;
      //      System.out.println("ou: "+ p);
      //p.x = p.x + 0.00001;
      //p.y = r*p.x - r*Math.pow(p.x, 2);
    }
    public String toString() {
      return "y, rx(1 - x)";
    }
  }

  final ColoredDensityMap mMap;
  final long mIterations;
  final Random rand;
  final double mBlend;

  Flame [] mFuncs;
  static final String LOADFILE = System.getProperty("loadfile", "");

  public ChaosGame(final ColoredDensityMap map,
                   final long iterations,
                   final double blend,
                   final long seed) {
    mMap = map;
    mIterations = iterations;
    mBlend = blend;
    rand = new Random(seed);
    init();
  }

  void init() {
    if (!LOADFILE.equals("")) {
      final Xml flameXml = new Xml(new java.io.File(LOADFILE));
      final org.w3c.dom.NodeList xforms = flameXml.getNodes("/flame/xform");
      mFuncs = new Flame[xforms.getLength()];
      for (int i = 0; i < xforms.getLength(); i++) {
        mFuncs[i] = new Linear(xforms.item(i));
      }
    } else {
      final List<Flame> functions = new ArrayList<Flame>();
      for (int i = 0, n = 1 + rand.nextInt(NUM_FUNCS); i < n; i++) {
        functions.add(randFlame());
      }
      mFuncs = functions.toArray(new Flame[functions.size()]);
    }
  }

  Flame randFlame() {
    if (FUNC_NDX < 0) {
      return lookupFlame(rand.nextInt(NUM_FUNCS));
    }
    return lookupFlame(FUNC_NDX);
  }

  Flame lookupFlame(final int ndx) {
    Flame f;
    switch (ndx % 18) {
    case 0: f = new Identity(0); break;
    case 1: f = new Linear(1); break;
    case 2: f = new Parabolic(2); break;
    case 3: f = new Sinusoidal(3); break;
    case 4: f = new Spherical(4); break;
    case 5: f = new Swirl(5); break;
    case 6: f = new Horseshoe(6); break;
    case 7: f = new Polar(7); break;
    case 8: f = new Hankerchief(8); break;
    case 9: f = new Heart(9); break;
    case 10: f = new Disc(10); break;
    case 11: f = new Spiral(11); break;
    case 12: f = new Hyperbolic(12); break;
    case 13: f = new Diamond(13); break;
    case 14: f = new Ex(14); break;
    case 15: f = new Julia(15); break;
    case 16: f = new LorenzAttractor(16); break;
    case 17: f = new LogisticMap(rand.nextDouble()
                                 * Double.parseDouble(System.getProperty("r", "3.7")), 17); break;
    default: throw new IllegalStateException();
    }
    if (RAND_COOEF) {
      f.randCooef(rand);
    }
    return f;
  }

  public Map<Integer,String> getFlameStrings() {
    final Map<Integer,String> funcStrs = new HashMap<Integer,String>();
    for (int i = 0; i < mFuncs.length; i++) {
      funcStrs.put(i, mFuncs[i].toString());
    }
    return funcStrs;
  }

  public String getFlameXml() {
    String s = "<flame>";
    for (int i = 0; i < mFuncs.length; i++) {
      s += "\n  "+ mFuncs[i].toXml();
    }
    s += "\n</flame>";
    return s;
  }

  public String toString() {
    return getFlameXml();
  }

  public void run() {
    final Point p = new Point(Double.parseDouble(System.getProperty("x", "0.5")), 0, 0);
    for (long i = 0; i < mIterations; i++) {
      Flame f = mFuncs[rand.nextInt(mFuncs.length)];
      Linear.setPoint(p);
      Linear.updateRadius();
      Linear.updateTheta();
      f.doOp(p);
      mMap.map(p.x, p.y, f.getId());
    }
  }

  public void rotate(double theta) {
    for (Flame f : mFuncs) {
      f.rotate(theta);
    }
  }

  double distance(final Point p) {
    return Math.sqrt(distanceSquared(p));
  }

  double distanceSquared(final Point p) {
    final double x = p.x;
    final double y = p.y;
    return x*x + y*y;
  }

  double theta(final Point p) {
    if (p.x == 0) {
      return 0;
    }
    return Math.atan(p.y/p.x);
  }
}
