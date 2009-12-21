package space;

import org.freality.util.Measure;

/**
 * The SceneScaling class provides a method of consistently scaling
 * Measures.
 */
public class SceneScaling {

  final Measure.Magnitude distanceMagnitude;

  public SceneScaling() {
    this(Measure.Magnitude.UNIT);
  }
  public SceneScaling(Measure.Magnitude distanceMagnitude) {
    this.distanceMagnitude = distanceMagnitude;
  }

  public Measure scale(Measure m) {
    if (m.unit == Measure.Unit.LENGTH)
      return m.convert(distanceMagnitude);
    return m;
  }
}
