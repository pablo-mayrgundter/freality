package fun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
class KG extends HashSet<Tuple> {

  Map<Object,Tuple> index;

  KG() {
    index = new HashMap<Object,Tuple>();
  }

  public boolean add(Tuple t) {
    for (Object a : t.args) {
      index.put(a, t);
    }
    return super.add(t);
  }

  public static void main(String [] args) throws Exception {
    KG kg = new KG();
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = r.readLine()) != null) {
      String [] parts = line.split(",");
      String a = parts[0], b = parts[0], c = parts[0];
      kg.add(new Tuple(a, b, c));
      Set<Tuple> matches = new HashSet<Tuple>();
      matches.addAll(kg);
      if (a.equals("?")) {
        // kg.get();
      }
      if (b.equals("?")) {
      }
      if (b.equals("?")) {
      }
    }
  }
}