package util;

public class Sleep {
  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
    }
  }
}