package lang.pl;

import java.io.*;
import java.util.*;
import java.util.regex.*;

class MetaCirc {

  static class Tree {

    static Pattern SEXPR = Pattern.compile("\\s*\\((\\s*(?:\\w|([^)]+)+)\\s*\\)\\s*");
    //                                        group(                      )
    static Pattern ARG = Pattern.compile("([A-Za-z0-9)(]\\s*)");

    Tree left;
    Tree right;

    static Tree parse(String prog) {
      Matcher sexprMatcher = SEXPR.matcher(prog);
      if (sexprMatcher.matches()) {
        String sexpr = sexprMatcher.group(1);
        System.out.println("sexpr: " + sexpr);
        Matcher argMatcher = ARG.matcher(sexpr);
        List<String> args = new ArrayList<String>();
        while (argMatcher.find()) {
          args.add(argMatcher.group(1).trim());
        }
        System.out.println("GOOD PROGRAM: " + sexpr);
        System.out.println("ARGS: " + args);
      } else {
        System.err.println("BAD PROGRAM: " + prog);
      }

      return new Tree();
    }
  }

  static class Env extends TreeMap<String, String> implements Map<String, String> {
  }

  static void run(String prog) {
    Tree progTree = Tree.parse(prog);
    Env env = new Env();
    eval(progTree, env);
  }

  static void eval(Tree progTree, Env e) {
  }

  public static void main(String [] args) throws Exception {
    System.out.println("Yo");
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = r.readLine()) != null) {
      run(line);
    }
  }
}
