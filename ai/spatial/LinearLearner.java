package ai.spatial;

import ai.*;

public abstract class LinearLearner<T> extends Learner<T> {

  T memory;

  public LinearLearner() {}

  public void setGoal(final T goal) {
    this.goal = goal;
  }
}
