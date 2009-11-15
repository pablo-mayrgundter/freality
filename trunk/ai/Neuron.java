package ai;

/**
 * A Neuron contains a learner and appears as an environment to other
 * neurons.
 */
class Neuron {
  final Environment env;
  final Learner learner;
  Neuron(final Environment env, final Learner learner) {
    this.env = env;
    this.learner = learner;
    learner.env = env;
  }
  /**
   * Should pass up diff if > 0, or in other words work with upstream
   * neuron.
   */
  public double getInput() {
    return learner.distFromGoal(this.env.get());
  }
  /**
   * Same.. should either pass down immediate response or relay from
   * upstream neuron.
   */
  public void setOutput(final double output) {
    this.env.set(0);
  }
}