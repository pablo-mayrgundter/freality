package ai.spatial;

public class LinearLearner extends Learner {
  public LinearLearner() {}
  /** Guess behavior based on how close to goal. */
  protected void learn () {
    final double stimulus = env.get();
    env.set((stimulus - distFromGoal(stimulus)) * 0.1);
  }
}
