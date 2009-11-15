package ai.spatial;

public class RandomLearner extends Learner {
  double state = 0;
  public RandomLearner() {}
  /** Guess behavior based on how close to goal. */
  protected void learn () {
    final double rand = Math.random() * 2.0 - 1.0;
    final double stimulus = env.get();
    state += distFromGoal(stimulus) * rand;
    env.set(state);
  }
}
