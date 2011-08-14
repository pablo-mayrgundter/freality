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

  public static enum Unit {

    LENGTH("l", "m", "meter"),
    MASS("m", "g", "gram"),
    TIME("t", "s", "second"),
    CURRENT("I", "A", "Ampere"),
    TEMPERATURE("T", "K", "Kelvin"),
    LUMINOUS_INTENSITY("L", "cd", "candela"),
    AMOUNT_OF_SUBSTANCE("n", "mol", "mole");

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

    static Unit lookup(final String s) {
      Unit u = null;
      u = lookupByName(s);
      if (u != null)
        return u;
      u = lookupByAbbreviation(s);
      return u;
    }

    static Unit lookupByAbbreviation(String s) {
      return UNIT_ABBREVIATION_LOOKUP.get(s);
    }

    static Unit lookupByName(String s) {
      return UNIT_NAME_LOOKUP.get(s);
    }
  }

  public static enum Magnitude {

    YOTTA(24, "yotta", "Y"),
    ZETTA(21, "zetta", "Z"),
    EXA(18, "exa", "E"),
    PETA(15, "peta", "P"),
    TERA(12, "tera", "T"),
    GIGA(9, "giga", "G"),
    MEGA(6, "mega", "M"),
    KILO(3, "kilo", "k"),
    HECTO(2, "hecto", "h"),
    DECA(1, "deca", "D"),
    UNIT(0, "", ""),
    DECI(-1, "deci", "d"),
    CENTI(-2, "centi", "c"),
    MILLI(-3, "milli", "m"),
    MICRO(-6, "micro", "\u03BC"),
    NANO(-9, "nano", "n"),
    PICO(-12, "pico", "p"),
    FEMTO(-15, "femto", "f"),
    ATTO(-18, "atto", "a"),
    ZETO(-21, "zepto", "z"),
    YOCTO(-24, "yocto", "y");

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
      return MAG_PREFIX_LOOKUP.get(s);
    }

    static Magnitude lookupByAbbreviation(String s) {
      return MAG_ABBREVIATION_LOOKUP.get(s);
    }
  }

  static final Pattern MEASURE_PATTERN =
    Pattern.compile("(-?\\d+(?:.\\d+)?(?:E\\d+)?)\\s*([khdnmgtpfaezy\u03BC]|(?:yotta|zetta|exa|peta|tera|giga|mega|kilo|hecto|deca|deci|centi|milli|micro|nano|pico|femto|atto|zepto|yocto))?\\s*([mgsAKLn]|(?:meter|gram|second|Ampere|Kelvin|candela|mole))", Pattern.CASE_INSENSITIVE); // TODO(pablo): case_insensitive.

  static final Map<String,Magnitude> MAG_PREFIX_LOOKUP = new HashMap<String,Magnitude>();
  static final Map<String,Magnitude> MAG_ABBREVIATION_LOOKUP = new HashMap<String,Magnitude>();
  static final Map<String,Unit> UNIT_ABBREVIATION_LOOKUP = new HashMap<String,Unit>();
  static final Map<String,Unit> UNIT_NAME_LOOKUP = new HashMap<String,Unit>();

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

    static final long serialVersionUID = 7643768565382046869L;

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
