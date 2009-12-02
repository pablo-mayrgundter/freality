package org.freality.gui.three;

/**
 * Convenience class for scaling a scene's metrics.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 * @version $Revision: 1.1.1.1 $
 */
public class SceneScaling {
  public double distance;
  public double object;
  public double time;
  public SceneScaling(double distance, double object, double time){
    this.distance = distance;
    this.object = object;
    this.time = time;
  }
}
