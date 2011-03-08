package phys;

// wip.
public class SI {

  static class Kilo extends Scale { Kilo(){super(3, "kilo", "k"); }}
  static class Mega extends Scale { Mega(){super(6, "mega", "M"); }}

  static class Meter extends Type { Meter(){super("l", "m", "meter");}}
  static class Gram extends Type { Gram(){super("m", "g", "gram");}}
  static class Second extends Type { Second(){super("t", "s", "second");}}

  static class Unit<S,T> {
    <A extends Scale, B extends Type> Unit<A,B> mult(Unit<A,B> o) {
      return o;
    }

    final double scalar;

    Unit(final double scalar) {
      this.scalar = scalar;
    }
  }

  static class Scale {

    public final int exponent;
    final String prefix;
    final String abbreviation;

    private Scale(int exponent, String prefix, String abbreviation) {
      this.exponent = exponent;
      this.prefix = prefix;
      this.abbreviation = abbreviation;
    }

    public String getAbbrev() {
      return abbreviation;
    }
  }

  static class Type {

    final String symbol;
    final String unitAbbreviation;
    final String unitName;

    Type(String symbol, String unitAbbreviation, String unitName) {
      this.symbol = symbol;
      this.unitAbbreviation = unitAbbreviation;
      this.unitName = unitName;
    }

    public String getAbbrev() {
      return unitAbbreviation;
    }
  }
}
