package algs.search;

import java.util.*;

/**
 * A simple reverse index for strings.
 *
 * @author Pablo Mayrgundter
 */
public class Index extends HashMap<String,String> {

  public void put(String doc) {
    for (String tok : tokenize(doc)) {
      put(tok, doc);
    }
  }

  public Set<String> lookup(String search) {
    Set<String> matches = new HashSet<String>();
    for (String tok : tokenize(search)) {
      String doc = get(tok);
      if (doc == null) {
        continue;
      }
      matches.add(doc);
    }
    return matches;
  }

  String [] tokenize(String s) {
    return s.split("\\s+");
  }

  public static void main(String [] args) {
    Index index = new Index();
    for (String arg : args) {
      index.put(arg);
    }
    for (String arg : args) {
      Set<String> matches = index.lookup(arg);
      System.out.println("query: "+ arg);
      System.out.println("matches:");
      for (String match : matches) {
        System.out.println("\t"+ match);
      }
      System.out.println("");
    }
  }
}