package ai;

/**
 * Hypothesis: adaptive learning is an iterated function system:
 *
 *   stimulus(response(stimulus,goal),environment)
 *
 *     where stimulus is convergent if |stimulus-goal| is < 1,
 *     divergent otherwise.
 *
 * meaning that stimulus is a function of response changing the
 * environment, and response is selected to converge that stimulus
 * towards a goal. To converge towards a goal within a complex
 * environment, stimulus and response may need to be complex.
 *
 * A (simple?) program to demonstrate this is to create a genetic
 * iterated function system that recombines on success and mutates on
 * failure.  The macro converge/diverge behavior of the function is
 * expected to be Chomsky/Wolfram Universal/Type-4, and it is also
 * expected that practical solutions will find the the local structure
 * modularly reused (e.g. chromosomally) to be a plausible mechanism
 * for the adapted complexity seen in nature.
 *
 * Version 0 (2008/08/11, archived)
 * Version 1 (2008/12/18, below): Perceptual Control Feedback of
 * random guessing used for local maximization.  Task: hill climb
 * sin(x).
 *
 * TODO: all 4 pattern types, multi-layer learning, genetic
 * variation/selection.
 *
 * @author Pablo Mayrgundter
 */
public abstract class Learner {

  /**
   * Risk is unknowable a priori and should be inversely proportional
   * to success/fecundity.
   */
  double risk, goal;

  Environment env;

  public Learner() {}

  /** Perceptual Control Feedback inner-loop. */
  protected abstract void learn ();

  /**
   * Init method used instead of constructor to avoid redundant
   * constructor chaining.
   */
  double distFromGoal (final double stimulus) {
    return (stimulus - (goal = Math.max(goal, stimulus * risk)));
  }
}
