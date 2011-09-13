package algs.search;

// 10 docs @ 10 Bytes ea. = 100 Byte corpus
// 
// 100M queries in 
class Substring {
  static final int iterations = Integer.parseInt(System.getProperty("n", "10000"));
  public static void main(String [] args) {
    String [] corpus = new String[]{"00000a0000",
                                    "11111b1111",
                                    "22222c2222",
                                    "33333d3333",
                                    "44444e4444",
                                    "55555f5555",
                                    "66666g6666",
                                    "77777h7777",
                                    "88888i8888",
                                    "99999j9999",
                                    };
    String [] queries = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    long time = System.currentTimeMillis();
    int x = 0;
    for (int i = 0; i < iterations; i++) {
      outer:
      for (String q : queries) { // search for each of 10 queries
        for (String doc : corpus) { // in each of 10 docs
          if (doc.indexOf(q) != -1) { // often breaks early
            break outer;
          }
        }
      }
    }
    time = System.currentTimeMillis() - time;
    System.out.printf("%d\n", time);
  }
}