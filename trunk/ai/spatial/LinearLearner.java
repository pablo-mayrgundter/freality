package ai.spatial;

import ai.*;

public abstract class LinearLearner<T> extends Learner<T> {
  public LinearLearner() {}
  /** Guess behavior based on how close to goal. */
  public void learn () {
    final T stimulus = env.get();
    final int cmp = compareToGoal(stimulus);
    if (cmp > 0)
      moveAway();
    else if (cmp < 0)
      moveTowards();
  }

  abstract void moveTowards();
  abstract void moveAway();
}
