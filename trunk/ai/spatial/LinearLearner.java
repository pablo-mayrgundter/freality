package ai.spatial;

import ai.*;

public abstract class LinearLearner<T> extends Learner<T,T> {

  T memory;

  public LinearLearner() {}

  public abstract T transform(T input);

  public void setGoal(final T goal) {
    this.goal = goal;
  }
}
