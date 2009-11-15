package util;

import java.util.ArrayList;
import java.util.List;

/**
 * The GoogleChartAPI class is a utility for rendering multiple
 * timeseries to the Google Chart API.  Currently has little
 * functionality.
 *
 * @author Pablo Mayrgundter
 */
public class GoogleChartAPI extends ArrayList<double []> implements List<double []> {

  final String names;
  final String colors;
  final int numSeries;
  final double [] mins;
  final double [] maxs;

  public GoogleChartAPI(final String names, final String colors) {
    numSeries = names.split(",").length;
    this.names = names.replaceAll(",", "|");
    this.colors = colors;
    mins = new double[numSeries];
    maxs = new double[numSeries];
    System.err.println("numSeries: "+ numSeries);
    for (int i = 0; i < numSeries; i++) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = Double.MIN_VALUE;
    }
  }

  public boolean add(double [] vals) {
    for (int row = 0; row < vals.length; row++) {
      if (vals[row] < mins[row])
        mins[row] = vals[row];
      if (vals[row] > maxs[row])
        maxs[row] = vals[row];
    }
    return super.add(vals);
  }

  /**
   * All series are normalized to have mins at 0.
   */
  public String toString() {
    String data =  "";
    String ranges = "";
    for (int i = 0, n = numSeries; i < n; i++) {
      if (maxs[i] - mins[i] < 0.01)
        maxs[i] = mins[i] + 0.01;
      for (int j = 0; j < size(); j++) {
        final double [] vals = get(j);
        data += String.format("%.2f%s", vals[i] - mins[i], (j == (size() - 1) ? "" : ","));
      }
      data += "|";
      //      if (mins[i] < 0) {
        maxs[i] += -mins[i];
        mins[i] = 0;
        //  }
        ranges += String.format("%.2f,%.2f", mins[i], maxs[i]) + (i < numSeries - 1 ? "," : "");
    }
    return String.format("http://chart.apis.google.com/chart?cht=lc&chs=300x300&chxt=x,y&chdl=%s&chco=%s&chds=%s&chd=t:%s",
                         names, colors, ranges, data);
  }
}
