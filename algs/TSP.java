package algs;

import static util.Check.*;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

class TSP {

  Matrix tsm;

  TSP(Matrix tsm) {
    this.tsm = tsm;
  }

  /**
   * @return a predicted next value for the current time-series.
   */
  double[] pred() {
    double[] preds = new double[tsm.getRows()];
    for (int i = 0, n = tsm.getRows(); i < n; i++) {
      double[] ts = tsm.get(i);
      double lastVal = ts[ts.length - 1];
      preds[i] = lastVal;
    }
    return preds;
  }

  /**
   * @return a - b
   */
  static double[] diff(double[] a, double[] b) {
    check(a.length == b.length, "Array lengths must be equal.");
    double[] d = new double[a.length];
    for (int i = 0; i < a.length; i++) {
      d[i] = a[i] - b[i];
    }
    return d;
  }

  public static void main(String [] args) throws Exception {
    Reader r = args.length == 1 ? new FileReader(args[0]) : new InputStreamReader(System.in);
    Matrix tsm = Matrix.read(r);
    TSP tsp = new TSP(tsm.subMatrix(tsm.getCols() - 1, tsm.getRows()));
    double[] preds = tsp.pred();
    double[] actual = tsm.getCol(tsm.getCols() - 1);
    double[] diff = diff(actual, preds);
    for (int i = 0; i < preds.length; i++) {
      System.out.printf("row(%d) actual(%f) pred(%f) diff(%f)\n",
                        i, actual[i], preds[i], diff[i]);
    }
  }
}
