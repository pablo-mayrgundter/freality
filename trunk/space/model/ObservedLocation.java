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
    buf.append("{").append(coordinate).append(",").append(epoch).append("}");
    return buf;
  }
}
