package ai;

import util.Flags;

public class Test {

  protected static final String ENV_CLASS = Flags.get("env", "ai.spatial.SineEnvironment");
  protected static final String LEARNER_CLASS = Flags.get("learner", "ai.spatial.RandomLearner");

  /**
   * Infinite learning loop with new Learner(0.01, 0, 0, 0.5)
   */
  public static void main (final String [] args) throws Exception {
    final Learner<String> l = new LanguageLearner();
    l.risk = 0.01;
    l.goal = "foo";
    l.environment = new Environment<String>();
    while (true) {
      System.err.println(String.format("goal:%.2f", l.goal));
      System.out.println(l.learn(args[0]));
      if (!Util.sleep(100))
        break;
    }
  }
}