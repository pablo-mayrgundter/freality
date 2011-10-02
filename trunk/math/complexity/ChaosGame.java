package math.complexity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import math.util.Xml;

public class ChaosGame implements Runnable {

  static int FUNC_NDX = -1;
  static int NUM_FUNCS = 15;
  static {
    final String funcProp = System.getProperty("func");
    if (funcProp != null) {
      FUNC_NDX = Integer.parseInt(funcProp);
      NUM_FUNCS = 2;
    }

    final String numFuncsProp = System.getProperty("funcs");
    if (numFuncsProp != null) {
      NUM_FUNCS = Integer.parseInt(numFuncsProp);
    }
  }

  public static interface Function {
    void doOp(Point p);
    void rotate(double theta);
    int getId();
    String toXml();
  }

  static final class Point {
    double x, y, z;
    Point() {
      this(0, 0, 0);
    }
    Point(final double x, final double y, final double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    public Object clone() {
      return new Point(x, y, z);
    }
    public String toString() {
      return x + "," + y + "," + z;
    }
  }

  static class Linear implements Function {
    Random rand;
    double weight, color, a, b, c, d, e, f;
    int id;
    public Linear(final org.w3c.dom.Node node) {
      rand = new Random();
      rand.setSeed(Long.parseLong(node.getAttributes().getNamedItem("seed").getTextContent()));
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

    Linear(Random r, int id) {
      this.rand = r;
      this.id = id;

      a = r.nextDouble();
      b = r.nextDouble();

      c = r.nextDouble();

      d = r.nextDouble();
      e = r.nextDouble();

      f = r.nextDouble();

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

    static double nextDoubleMaybeNeg(final Random r) {
      return (r.nextBoolean() ? -1f : 1f) * r.nextDouble();
    }

    /**
     * Other types override this.
     */
    public void variation(final Point p) {
      p.x = a * p.x + b * p.y + c;
      p.y = d * p.x + e * p.y + f;
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
    Identity(Random r, int id) {
      super(r, id);
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
    Parabolic(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      p.x = a * Math.pow(p.y, 2);
      p.y = b * Math.pow(p.x, 2);
    }
    public void rotate(double theta) {
      a = a * Math.cos(theta) - b * Math.sin(theta);
      b = a * Math.sin(theta) + b * Math.cos(theta);
    }
    public String toString() {
      return "y^2, x^2";
    }
  }

  static final class LogisticMap extends Linear {
    final double r;
    LogisticMap(double r, Random rand, int id) {
      super(rand, id);
      this.r = r;
    }
    public void variation(final Point p) {
      //      System.out.println("in: "+ p);
      double x = p.y;
      double y = r*p.x - r*Math.pow(p.x, 2);
      p.x = y;
      p.y = y;
      //      System.out.println("ou: "+ p);
      //p.x = p.x + 0.00001;
      //p.y = r*p.x - r*Math.pow(p.x, 2);
    }
    public String toString() {
      return "y, rx(1 - x)";
    }
  }

  static final class Sinusoidal extends Linear {
    Sinusoidal(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      p.x = a * Math.sin(p.x);
      p.y = b * Math.sin(p.y);
    }
    public String toString() {
      return "sin(x), sin(y)";
    }
  }

  static final class Spherical extends Linear {
    Spherical(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double distanceSquared = distanceSquared(p);
      if (distanceSquared == 0) {
        distanceSquared = 0.01f;
      }
      p.x = p.x / distanceSquared;
      p.y = p.y / distanceSquared;
    }
    public String toString() {
      return "(x)/r^2, (y)/r^2";
    }
  }

  static final class Swirl extends Linear {
    Swirl(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = r * Math.cos(t + r);
      p.y = r * Math.sin(t + r);
    }
    public String toString() {
      return "r*cos(θ+r), r*sin(θ+r)";
    }
  }

  static final class Horseshoe extends Linear {
    Horseshoe(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = r * Math.cos(2f * t);
      p.y = r * Math.sin(2f * t);
    }
    public String toString() {
      return "r*cos(2*θ), r*sin(2*θ)";
    }
  }

  static final class Polar extends Linear {
    Polar(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      p.x = theta(p) / Math.PI;
      p.y = distance(p) - 1f;
    }
    public String toString() {
      return "θ/π, r-1";
    }
  }

  static final class Hankerchief extends Linear {
    Hankerchief(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      final double r = distance(p);
      final double t = theta(p);
      p.x = r * Math.sin(t + r);
      p.y = r * Math.cos(t - r);
    }
    public String toString() {
      return "r*sin(θ+r), r*cos(θ-r)";
    }
  }

  static final class Heart extends Linear {
    Heart(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = r * Math.sin(t * r);
      p.y = -r * Math.cos(t * r);
    }
    public String toString() {
      return "r*sin(θ*r), -r*cos(θ*r)";
    }
  }

  static final class Disc extends Linear {
    Disc(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = t * Math.sin(Math.PI * r) / Math.PI;
      p.y = t * Math.cos(Math.PI * r) / Math.PI;
    }
    public String toString() {
      return "θ*sin(π/r)/π, θ*cos(πr)/π";
    }
  }

  static final class Spiral extends Linear {
    Spiral(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = (Math.cos(t) + Math.sin(r)) / r;
      p.y = (Math.sin(t) - Math.cos(r)) / r;
    }
    public String toString() {
      return "(cos(θ) + sin(r))/r, (sin(θ) - cos(r))/r";
    }
  }

  static final class Hyperbolic extends Linear {
    Hyperbolic(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = Math.sin(t) / r;
      p.y = Math.cos(t) * r;
    }
    public String toString() {
      return "sin(θ)/r, cos(θ)*r";
    }
  }

  static final class Diamond extends Linear {
    Diamond(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = Math.sin(t) / Math.cos(r);
      p.y = Math.cos(t) / Math.sin(r);
    }
    public String toString() {
      return "sin(θ)/cos(r), cos(θ)/sin(r)";
    }
  }

  static final class Ex extends Linear {
    Ex(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double r = sharedRadius * a;
      double t = sharedTheta * b;
      p.x = r * Math.pow(Math.sin(t + r), 3);
      p.y = r * Math.pow(Math.cos(t - r), 3);
    }
    public String toString() {
      return "sin(θ+r)^3, cos(θ-r)^3";
    }
  }

  static final class Julia extends Linear {
    Julia (Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      final double sqr = Math.sqrt(sharedRadius) * a;
      final double t = sharedTheta * b;
      final double O = rand.nextDouble() <= 0.5 ? 0f : Math.PI;
      p.x = sqr * Math.cos(t / 2f + O);
      p.y = sqr * Math.sin(t / 2f + O);
    }
    public String toString() {
      return "sqr*cos(θ/2+Ω), sqr*sin(θ/2+Ω); Ω=0|π";
    }
  }

  static final class LorenzAttractor extends Linear {
    LorenzAttractor(Random r, int id) {
      super(r, id);
    }
    public void variation(final Point p) {
      double dX = 0.01 * (10.0*(p.y - p.x));
      double dY = 0.01 * (28.0*p.x - p.y - p.x*p.z);
      double dZ = 0.01 * (-2.6*p.z + p.x*p.y);
      p.x += dX;
      p.y += dY;
      p.z += dZ;
    }
    public String toString() {
      return "d[x,y,z]/dt=[10(y-x),28x-y-xz,8/3z+xy]";
    }
  }

  final ColoredDensityMap mMap;
  final int mIterations;
  final Random rand;
  final double mBlend;

  Function [] mFuncs;
  static final String LOADFILE = System.getProperty("loadfile", "");

  public ChaosGame(final ColoredDensityMap map,
                   final int iterations,
                   final double blend) {
    mMap = map;
    mIterations = iterations;
    mBlend = blend;
    rand = new Random();
    init();
  }

  public void setSeed(long seed) {
    rand.setSeed(seed);
  }

  public void init() {
    if (!LOADFILE.equals("")) {
      final Xml flameXml = new Xml(new java.io.File(LOADFILE));
      final org.w3c.dom.NodeList xforms = flameXml.getNodes("/flame/xform");
      mFuncs = new Function[xforms.getLength()];
      for (int i = 0; i < xforms.getLength(); i++) {
        mFuncs[i] = new Linear(xforms.item(i));
      }
    } else {
      final List<Function> functions = new ArrayList<Function>();
      for (int i = 0, n = 1 + rand.nextInt(NUM_FUNCS); i < n; i++) {
        functions.add(randFunction());
      }
      mFuncs = functions.toArray(new Function[functions.size()]);
    }
  }

  Function randFunction() {
    if (FUNC_NDX < 0) {
      return lookupFunction(rand.nextInt(NUM_FUNCS));
    }
    return lookupFunction(FUNC_NDX);
  }

  Function lookupFunction(final int ndx) {
    switch (ndx % 18) {
    case 0: return new Identity(rand, 0);
    case 1: return new Linear(rand, 1);
    case 2: return new Parabolic(rand, 2);
    case 3: return new Sinusoidal(rand, 3);
    case 4: return new Spherical(rand, 4);
    case 5: return new Swirl(rand, 5);
    case 6: return new Horseshoe(rand, 6);
    case 7: return new Polar(rand, 7);
    case 8: return new Hankerchief(rand, 8);
    case 9: return new Heart(rand, 9);
    case 10: return new Disc(rand, 10);
    case 11: return new Spiral(rand, 11);
    case 12: return new Hyperbolic(rand, 12);
    case 13: return new Diamond(rand, 13);
    case 14: return new Ex(rand, 14);
    case 15: return new Julia(rand, 15);
    case 16: return new LorenzAttractor(rand, 16);
    case 17: return new LogisticMap(rand.nextDouble() * Double.parseDouble(System.getProperty("r", "3.7")), rand, 17);
    default: throw new IllegalStateException();
    }
  }

  public Map<Integer,String> getFunctionStrings() {
    final Map<Integer,String> funcStrs = new HashMap<Integer,String>();
    for (int i = 0; i < mFuncs.length; i++) {
      funcStrs.put(i, mFuncs[i].toString());
    }
    return funcStrs;
  }

  public String getFunctionXml() {
    String s = "<flame>";
    for (int i = 0; i < mFuncs.length; i++) {
      s += "\n  "+ mFuncs[i].toXml();
    }
    s += "\n</flame>";
    return s;
  }

  public String toString() {
    return getFunctionXml();
  }

  public final void run() {
    final Point p = new Point(Double.parseDouble(System.getProperty("x", "0.5")), 0, 0);
    for (int i = 0; i < mIterations; i++) {
      Function f = mFuncs[rand.nextInt(mFuncs.length)];
      Linear.setPoint(p);
      Linear.updateRadius();
      Linear.updateTheta();
      f.doOp(p);
      mMap.map(p.x, p.y, f.getId());
    }
  }

  public void rotate(double theta) {
    for (Function f : mFuncs) {
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
// Sierpenski's Gasket
/*
    Function lookupFunction(final int ndx) {
        switch (ndx % 3) {
        case 0: return new BaseFunction() {
                public void op(final Point p) {
                    p.x = p.x / 2f;
                    p.y = p.y / 2f;
                }
            };
        case 1: return new BaseFunction() {
                public void op(final Point p) {
                    p.x = (p.x + 1f) / 2f;
                    p.y = p.y / 2f;
                }
            };
        case 2: return new BaseFunction() {
                public void op(final Point p) {
                    p.x = p.x / 2f;
                    p.y = (p.y + 1f) / 2f;
                }
            };
        default: throw new IllegalStateException();
        }
    }
*/
