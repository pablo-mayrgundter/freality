package space.model;

public class ObservedLocation {

  public final Coordinate coordinate;
  public final Epoch epoch;

  public ObservedLocation(Coordinate coordinate, Epoch epoch) {
    this.coordinate = coordinate;
    this.epoch = epoch;
  }

  public String toString() {
    return toString(new StringBuffer()).toString();
  }

  public StringBuffer toString(StringBuffer buf) {
    buf.append("{\n\"coord\": ").append(coordinate);
    buf.append(",\n\"epoch\": ").append(epoch).append("\n}");
    return buf;
  }

  static final ObservedLocation TEST = new ObservedLocation(Coordinate.TEST, Epoch.TEST);
  public static void main(String [] args) {
    System.out.println(TEST);
  }
}
