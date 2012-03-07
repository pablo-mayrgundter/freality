package algs.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

public class IndexTest extends TestCase {

  Index index;

  public void setUp() {
    index = new Index();
  }

  public void tearDown() {
    index = null;
  }

  public void testSearch() {
    String [] docs = new String[]{"a", "b", "c"};
    for (String s : docs) {
      index.putSimple(s);
    }
    for (String s : docs) {
      Set matches = index.lookupSimple(s);
      assertTrue("Index should contain indexed doc", matches.contains(s));
      assertEquals(1, matches.size());
    }
  }

  String newDoc(int size) {
    return new StringBuffer(size).toString();
  }

  static final int DOC_SIZE = Integer.parseInt(System.getProperty("size", "10"));
  static final int NUM_DOCS = Integer.parseInt(System.getProperty("docs", "10"));
  static final int NUM_QUERIES= Integer.parseInt(System.getProperty("queries", "10"));

  public void testLoad() {
    loadTest(DOC_SIZE, NUM_DOCS, NUM_QUERIES);
  }

  public void loadTest(int docSize, int numDocs, int numQueries) {
    System.err.printf("\n\nsize=%d, docs=%d, queries=%d:\n",
                      docSize, numDocs, numQueries);
    int i = 0;
    long time;

    System.err.println("Generating docs...");
    time = System.currentTimeMillis();
    List<String> docs = new ArrayList<String>();
    for (i = 0; i < numDocs; i++) {
      docs.add(newDoc(10000));
    }
    time = System.currentTimeMillis() - time;
    System.err.println("Corpus create time: "+ time);

    i = 0;
    time = System.currentTimeMillis();
    for (String doc : docs) {
      index.putSimple(doc);
    }
    time = System.currentTimeMillis() - time;
    System.err.println("Indexing time: "+ time);

    time = System.currentTimeMillis();
    for (i = 0; i < numQueries; i++) {
      index.lookupSimple("d");
    }
    time = System.currentTimeMillis() - time;
    System.err.println("Search time: "+ time);
  }

  /**
   * Runnable as:
   *
   *   java algs.search.IndexTest
   */
  public static void main(String [] args) {
    junit.textui.TestRunner.run(IndexTest.class);
  }
}
