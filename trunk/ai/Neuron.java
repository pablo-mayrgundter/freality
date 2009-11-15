package ai;

/**
 * A Neuron contains a learner and appears as an environment to other
 * neurons.
 */
class Neuron<T> {

  final Environment<T> env;
  final Learner<T> learner;

  Neuron(final Environment<T> env, final Learner<T> learner) {
    this.env = env;
    this.learner = learner;
    learner.env = env;
  }

  /**
   * Should pass up diff if > 0, or in other words work with upstream
   * neuron.
   */
  public double getInput() {
    return 0;
    //    return learner.compareToGoal(env.get());
  }

  /**
   * Same.. should either pass down immediate response or relay from
   * upstream neuron.
   */
  public void setOutput(final T output) {
    env.set(output);
  }
}