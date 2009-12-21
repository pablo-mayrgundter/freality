package space.model;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The time at which an observation is made.  J2000 is the default for
 * most of the system.
 */
public class Epoch {

  public static final Epoch J2000;

  static {
    Date d = null;
    try {
      d = new SimpleDateFormat("HH:mm:ssaa zzz, MMM d, yyyy").parse("12:00:00AM UTC, Jan 1, 2000");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    J2000 = new Epoch("J2000", d);
  }

  /** J2000 Epoch: 12:00 UT, Jan 1, 2000 */
  final String name;
  final Date dateOfEpoch;

  public Epoch(String name, Date dateOfEpoch) {
    this.name = name;
    this.dateOfEpoch = dateOfEpoch;
  }

  public String toString() {
    return toString(new StringBuffer()).toString();
  }

  public StringBuffer toString(StringBuffer buf) {
    buf.append(name);
    return buf;
  }

  public static void main(String [] args) {
    System.out.println(J2000);
  }
}
