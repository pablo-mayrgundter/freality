package algs.search;

import java.util.*;
import util.Shell;

/**
 * A simple reverse index for objects.
 *
 * @author Pablo Mayrgundter
 */
@SuppressWarnings("unchecked")
public class Index extends HashMap<Object, Set<Map>> {

  static final long serialVersionUID = 2912424671023552687L;

  public void putSimple(Object doc) {
    Map m = new HashMap(1);
    m.put("", doc);
    put(m);
  }

  public void put(Map doc) {
    for (Object lookupToken : tokenize(doc)) {
      Set<Map> postingList = get(lookupToken);
      if (postingList == null) {
        put(lookupToken, postingList = new HashSet<Map>());
      }
      postingList.add(doc);
    }
  }

  public Set lookupSimple(Object query) {
    Map m = new HashMap(1);
    m.put("", query);
    Set allValues = new HashSet();
    for (Map match : lookup(m)) {
      allValues.addAll(match.values());
    }
    return allValues;
  }

  class Pair {
    Object a, b;
    Pair(Object a, Object b) {
      this.a = a;
      this.b = b;
    }
    public boolean equals(Object other) {
      if (!(other instanceof Pair)) {
        return false;
      }
      Pair o = (Pair) other;
      return a.equals(o.a) && b.equals(o.b);
    }
    public int hashCode() {
      return a.hashCode() + b.hashCode();
    }
    public String toString() {
      return "[" + a + "," + b + "]";
    }
  }

  List<Object> tokenize(Map m) {
    List<Object> toks = new ArrayList<Object>();
    for (Object key : m.keySet()) {
      Object val = m.get(key);
      toks.add(new Pair(key, val));
    }
    return toks;
  }

  public Set<Map> lookup(Map query) {
    Set<Map> matches = new HashSet<Map>();
    boolean first = true;
    for (Object lookupToken : tokenize(query)) {
      Set<Map> postingList = get(lookupToken);
      if (postingList == null) {
        // Unknown token.
        break;
      }
      for (Map m : postingList) {
        if (first) {
          matches.addAll(postingList);
          first = false;
        } else {
          matches.retainAll(postingList);
        }
      }
    }
    return matches;
  }

  public static void main(String [] args) {
    Index index = new Index();
    Shell shell = new Shell();
    String line;
    while ((line = shell.readLine()) != null) {
      Map m = new HashMap();
      if (line.startsWith("?")) {
        line = line.substring(1, line.length()).trim();
        m.put("", line);
        Set<Map> matches = index.lookup(m);
        System.out.println("matches:");
        for (Map match : matches) {
          System.out.println("\t" + match);
        }
      } else {
        m.put("", line);
        index.put(m);
      }
    }
  }
}