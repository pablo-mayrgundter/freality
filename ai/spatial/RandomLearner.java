package ai.spatial;

import ai.*;

public abstract class RandomLearner<T> extends LinearLearner<T> {

  public RandomLearner() {}

  /** Guess behavior based on how close to goal. */
  public T learn(T in) {
    if (differenceFromGoal() < 0.0) {
      moveAway();
    } else {
      moveTowards();
    }
    return in; // no change?
  }

  public double differenceFromGoal() {
    return Math.random() - 0.5;
  }

  protected abstract void moveAway();
  protected abstract void moveTowards();
}
