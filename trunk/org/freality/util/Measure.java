package org.freality.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Measure formatting and conversion utility.  The system of measure
 * used is a a slight variation of the System International (SI)
 * system (http://scienceworld.wolfram.com/physics/SI.html).  There
 * are two particular variations.
 *
 * This first variation is that no unit has an implicit magnitude.
 * This is in contrast to the MKS or Meters, Kilograms, Seconds system
 * which has the "kilo" magnitude for its mass unit, or the CGS or
 * Centimeters, Grams, Seconds which has the "centi" magnitude for its
 * length unit.
 *
 * The second variation is that the "deca" magnitude's abbreviation is
 * defined as "D" instead of "da" so that a decagram can be
 * represented as "Dg" instead of "dag" or "da gram", which is
 * congruent with the usage of the other unit abbreviations.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class Measure {

  public static class Unit {

    public static Unit LENGTH = new Unit("l", "m", "meter");
    public static Unit MASS = new Unit("m", "g", "gram");
    public static Unit TIME = new Unit("t", "s", "second");
    public static Unit CURRENT = new Unit("I", "A", "Ampere");
    public static Unit TEMPERATURE = new Unit("T", "K", "Kelvin");
    public static Unit LUMINOUS_INTENSITY = new Unit("L", "cd", "candela");
    public static Unit AMOUNT_OF_SUBSTANCE = new Unit("n", "mol", "mole");

    final String symbol;
    final String unitAbbreviation;
    final String unitName;

    Unit(String symbol, String unitAbbreviation, String unitName) {
      this.symbol = symbol;
      this.unitAbbreviation = unitAbbreviation;
      this.unitName = unitName;
      UNIT_ABBREVIATION_LOOKUP.put(unitAbbreviation, this);
      UNIT_NAME_LOOKUP.put(unitName, this);
    }

    static Unit lookup(String s) {
      Unit u = null;
      u = lookupByName(s);
      if (u != null)
        return u;
      u = lookupByAbbreviation(s);
      return u;
    }

    static Unit lookupByAbbreviation(String s) {
      return (Unit) UNIT_ABBREVIATION_LOOKUP.get(s);
    }

    static Unit lookupByName(String s) {
      return (Unit) UNIT_NAME_LOOKUP.get(s);
    }
  }

  public static class Magnitude {

    public static Magnitude YOTTA = new Magnitude(24, "yotta", "Y");
    public static Magnitude ZETTA = new Magnitude(21, "zetta", "Z");
    public static Magnitude EXA = new Magnitude(18, "exa", "E");
    public static Magnitude PETA = new Magnitude(15, "peta", "P");
    public static Magnitude TERA = new Magnitude(12, "tera", "T");
    public static Magnitude GIGA = new Magnitude(9, "giga", "G");
    public static Magnitude MEGA = new Magnitude(6, "mega", "M");
    public static Magnitude KILO = new Magnitude(3, "kilo", "k");
    public static Magnitude HECTO = new Magnitude(2, "hecto", "h");
    public static Magnitude DECA = new Magnitude(1, "deca", "D");
    public static Magnitude UNIT = new Magnitude(0, "", "");
    public static Magnitude DECI = new Magnitude(-1, "deci", "d");
    public static Magnitude CENTI = new Magnitude(-2, "centi", "c");
    public static Magnitude MILLI = new Magnitude(-3, "milli", "m");
    public static Magnitude MICRO = new Magnitude(-6, "micro", "\u03BC");
    public static Magnitude NANO = new Magnitude(-9, "nano", "n");
    public static Magnitude PICO = new Magnitude(-12, "pico", "p");
    public static Magnitude FEMTO = new Magnitude(-15, "femto", "f");
    public static Magnitude ATTO = new Magnitude(-18, "atto", "a");
    public static Magnitude ZETO = new Magnitude(-21, "zepto", "z");
    public static Magnitude YOCTO = new Magnitude(-24, "yocto", "y");

    public final int exponent;
    final String prefix;
    final String abbreviation;

    private Magnitude(int exponent, String prefix, String abbreviation) {
      this.exponent = exponent;
      this.prefix = prefix;
      this.abbreviation = abbreviation;
      MAG_PREFIX_LOOKUP.put(prefix, this);
      MAG_ABBREVIATION_LOOKUP.put(abbreviation, this);
    }

    /**
     * Converts the given scalar in the given magnitude to the
     * equivalent scalar in this magnitude.
     */
    public double convert(double scalar, Magnitude mag) {
      return scalar * Math.pow(10, mag.exponent - exponent);
    }

    static Magnitude lookup(String s) {
      Magnitude m = null;
      m = lookupByPrefix(s);
      if (m != null)
        return m;
      m = lookupByAbbreviation(s);
      return m;
    }

    static Magnitude lookupByPrefix(String s) {
      return (Magnitude) MAG_PREFIX_LOOKUP.get(s);
    }

    static Magnitude lookupByAbbreviation(String s) {
      return (Magnitude) MAG_ABBREVIATION_LOOKUP.get(s);
    }
  }

  static final Pattern MEASURE_PATTERN =
    Pattern.compile("(-?\\d+(?:.\\d+)?(?:E\\d+)?)\\s*([khdnmgtpfaezy\u03BC]|(?:yotta|zetta|exa|peta|tera|giga|mega|kilo|hecto|deca|deci|centi|milli|micro|nano|pico|femto|atto|zepto|yocto))?\\s*([mgsAKLn]|(?:meter|gram|second|Ampere|Kelvin|candela|mole))", Pattern.CASE_INSENSITIVE); // HACK: case_insensitive.

  static final Map MAG_PREFIX_LOOKUP = new HashMap();
  static final Map MAG_ABBREVIATION_LOOKUP = new HashMap();
  static final Map UNIT_ABBREVIATION_LOOKUP = new HashMap();
  static final Map UNIT_NAME_LOOKUP = new HashMap();

  public final double scalar;
  public final Unit unit;
  public final Magnitude magnitude;

  public Measure(double scalar, Unit unit) {
    this(scalar, unit, Magnitude.UNIT);
  }

  public Measure(double scalar, Unit unit, Magnitude magnitude) {
    if (unit == null)
      throw new NullPointerException("Null unit given.");
    if (magnitude == null)
      throw new NullPointerException("Null magnitude given.");
    this.scalar = scalar;
    this.unit = unit;
    this.magnitude = magnitude;
  }

  public Measure convert(Magnitude mag) {
    return new Measure(mag.convert(scalar, magnitude), unit, mag);
  }

  public String toString() {
    return scalar + magnitude.abbreviation + unit.unitAbbreviation;
  }

  static class MeasureFormatException extends IllegalArgumentException {
    MeasureFormatException(String msg) {
      super(msg);
    }
  }

  /**
   * @throws MeasureFormatException If the string does not contain a parsable measure.
   */
  public static Measure parseMeasure(String s) {
    if (s == null)
      throw new NullPointerException("Given string is null");
    final Matcher m = MEASURE_PATTERN.matcher(s);
    if (!m.find())
      throw new MeasureFormatException("Could not parse measure from given string: " + s);

    //        System.err.println("#groups: " + m.groupCount());

    final String scalar = m.group(1);

    //        System.err.println("scalar: " + scalar);

    if (m.groupCount() == 2) {
      final String unit = m.group(2);
      //            System.err.println("unit: " + unit);
      return new Measure(Double.parseDouble(scalar), Unit.lookup(unit));
    }

    final String magnitude = m.group(2);
    //        System.err.println("magnitude: " + magnitude);

    final String unit = m.group(3);
    //        System.err.println("unit: " + unit);

    return new Measure(scalar == null ? 0.0 : Double.parseDouble(scalar),
                       Unit.lookup(unit),
                       magnitude == null ? Magnitude.UNIT : Magnitude.lookup(magnitude));
  }

  public static void main(String [] args) {
    final Measure m = parseMeasure(args[0]);
    System.out.println("\"" + args[0] + "\" -> "
                       + m + " "
                       + "(" + m.scalar + " " + m.magnitude.prefix + " " + m.unit.unitName + ") = "
                       + m.convert(Magnitude.UNIT));
  }
}
