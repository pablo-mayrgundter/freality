package ai;

public class Test {

  protected static final String ENV_CLASS = System.getProperty("env", "ai.spatial.SineEnvironment");
  protected static final String LEARNER_CLASS = System.getProperty("learner", "ai.spatial.LinearLearner");

  /**
   * Infinite learning loop with new Learner(0.01, 0, 0, 0.5)
   */
  public static void main (final String [] args) throws Exception {
    final Environment env = (Environment) Class.forName(ENV_CLASS).newInstance();
    final Learner l = (Learner) Class.forName(LEARNER_CLASS).newInstance();
    l.risk = 0.01;
    l.goal = 2.1;
    l.env = env;
    while (true) {
      System.out.println(String.format("stim:%.2f, goal:%.2f", env.state, l.goal));
      l.learn();
      if (!Util.sleep(100))
        break;
    }
  }
}