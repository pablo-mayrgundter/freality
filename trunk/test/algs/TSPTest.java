package algs;

import unit.TestCase;

public class TSPTest extends TestCase {

  public void testRead() {
    TSP tsp = new TSP();
    double[][] correct = new double[][]{{0,1,2,3,4},{1,2,3,4,5}};
    tsp.read("0,1,2,3,4;1,2,3,4,5");
    double[][] tss = tsp.tss;
    for (int i = 0; i < tss.length; i++) {
      for (int j = 0; j < tss[i].length; j++) {
        assertEquals(correct[i][j], tss[i][j]);
      }
    }
  }

  public void testPred() {
    TSP tsp = new TSP();
    tsp.read("0,1,2,3,4;1,2,3,4,5");
    double[] preds = tsp.pred();
    double[] expected = {4,5};
    assertEquals(expected, preds);
  }
}
