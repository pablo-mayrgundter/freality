package algs;

import unit.TestCase;

public class TSPTest extends TestCase {

  public void testRead() {
    TSP tsp = new TSP("0,1,2,3,4;1,2,3,4,5");
    double[][] correct = new double[][]{{0,1,2,3,4},{1,2,3,4,5}};
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        assertEquals(correct[i][j], tsp.tsm.get(i, j));
      }
    }
  }

  public void testPred() {
    TSP tsp = new TSP("0,1,2,3,4;1,2,3,4,5");
    double[] preds = tsp.pred();
    double[] expected = {4,5};
    assertEquals(expected, preds);
  }
}
