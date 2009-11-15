package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ObjectLoader {

  /** @throws RuntimeExeption */
  public static<T> void save(final String filename, final T obj) {
    try {
      final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
      oos.writeObject(obj);
      oos.close();
    } catch (final Exception e) {
      throw new RuntimeException("Exception during load: "+ e);
    }
  }

  /**
   * @throws RuntimeExeption
   */
  public static<T> T load(final String filename) {
    T obj = null;
    try {
      final ObjectInputStream iis = new ObjectInputStream(new FileInputStream(filename));
      obj = (T) iis.readObject();
      iis.close();
    } catch (final Exception e) {
      throw new RuntimeException("Exception during load: "+ e);
    }
    return obj;
  }
}