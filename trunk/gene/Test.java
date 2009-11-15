package gene;

class Test {

  static final int ITR = Integer.parseInt(System.getProperty("itr", "10"));

  public static void main(final String [] args) {
    final String target = args[0];
    final Pool pool = new Pool();

    //    final String [] parts = Util.bigrams(target);
    final String [] parts = target.split("\\s");
    for (int i = 0; i < parts.length; i++)
      pool.add(new StringGene(parts[i]));
    System.out.println(pool);
    int popMax;
    for (int i =0 ; i < ITR; i++) {
      popMax = 0;
      for (final Gene g : pool) {
        final String str = ((StringGene)g).genome;
        int strLen = str.length();
        int kMax = 0, k;
        System.err.println(str);
        for (int j = 0, n = target.length() - strLen; j < n; j++) {
          k = 1;
          while (target.regionMatches(j, str, 0, k++) && k < strLen);
          if (k > kMax)
            kMax = k;
        }
        g.fitness = kMax;
        if (kMax > popMax)
          popMax = kMax;
      }
      for (final Gene g : pool)
        g.fitness -= popMax / 3;
      System.out.println(pool);
      System.err.println(pool);
      pool.generation();
    }
  }
}