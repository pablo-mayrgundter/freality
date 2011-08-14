package space.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Time {

  /**
   * Days since the epoch, seems wrong.  From: http://home.att.net/~srschmitt/script_planet_orbits.html.
   */
  public static double dayNumber(Date currentDay) {
    final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
    cal.setTime(currentDay);
    final double year  = cal.get(Calendar.YEAR);
    final double month = cal.get(Calendar.MONTH);
    final double day   = cal.get(Calendar.DAY_OF_MONTH);
    final double hour  = cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND) * 3600;
    return 367.0 * year
      - 7.0 * (int)((year + (int)((month + 9.0) / 12.0)) / 4.0)
      + (275.0 * month / 9.0)
      + day - 730531.5
      + hour / 24.0;
  }

  public static void main(String [] args) {
    System.out.println("dayNumber: " + dayNumber(new Date()));
  }
}
