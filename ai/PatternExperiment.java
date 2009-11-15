package ai;

import java.util.ArrayList;
import java.util.List;
import math.automata.Wolfram;
import math.automata.viz.Drawer;
import util.Bits;

class PatternExperiment {

  static int atoi (final String s) {
    return Integer.parseInt(s);
  }

  public static void main (String [] args) {
    final String ruleProp = System.getProperty("rules","40:22");
    final String inputProp = System.getProperty("bits","auto");
    final String iterationsProp = System.getProperty("iterations","1");

    String [] ruleStrs = null;
    int width = 0;
    if (ruleProp.indexOf(":") != -1) {
      width = Integer.parseInt(ruleProp.split(":")[0]);
      ruleStrs = new String[width];
      final String rule = ruleProp.split(":")[1];
      for (int i = 0; i < ruleStrs.length; i++)
        ruleStrs[i] = rule;
    } else {
      ruleStrs = ruleProp.split(",");
      width = ruleStrs.length;
    }

    final int iterations = atoi(iterationsProp);

    final List<Bits> mem = new ArrayList<Bits>();
    for (int i = 0; i <= iterations; i++)
      mem.add(new Bits(width));

    // Setup src bits.
    final Bits inputRow = mem.get(0);
    if (inputProp.equals("auto")) {
      inputRow.set(inputRow.getLength() / 2);
    } else
      for (int i = 0; i < inputProp.length(); i++)
        inputRow.set(i, atoi(inputProp.charAt(i)+""));

    // Setup rules.
    final byte [] rules = new byte[width];
    for (int i = 0; i < ruleStrs.length; i++)
      rules[i] = (byte) atoi(ruleStrs[i]);

    final Drawer d = new Drawer(mem);
    d.clear();
    System.out.printf("% 5d: %s\n", 0, d.drawBitsWithRules(mem.get(0), new byte[rules.length], " "));
    for (int itr = 0; itr < iterations; itr++) {
      Bits src = mem.get(itr);
      Bits dst = mem.get(itr + 1);
      for (int i = 0; i < width; i++)
        Wolfram.apply(src, i, dst, i, rules[i]);
      System.out.printf("% 5d: %s\n", (itr+1), d.drawBitsWithRules(dst, rules, " "));
    }
  }
}
