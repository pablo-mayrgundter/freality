package ai.spatial;

import ai.*;

public abstract class RandomLearner<T> extends LinearLearner<T> {
  public RandomLearner() {}
  /** Guess behavior based on how close to goal. */
  public void learn () {
    final T stimulus = env.get();
    final int cmp = compareToGoal(stimulus);
    if (Math.random() < 0.5)
      moveAway();
    else
      moveTowards();
  }
}
