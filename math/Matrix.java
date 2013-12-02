package algs;

import static util.Check.*;

import java.util.Arrays;

public class Matrix {

  final double [][] buf;

  public Matrix(double[][] buf) {
    check(buf.length > 0, "Matrix must have at least 1 row");
    this.buf = buf;
  }

  public Matrix(int cols, int rows) {
    this(new double[rows][cols]);
  }

  public double get(int row, int col) {
    return buf[row][col];
  }

  public double[] get(int rowNdx) {
    return buf[rowNdx];
  }

  public double[] getCol(int rowNdx) {
    double[] col = new double[buf.length];
    for (int i = 0; i < buf.length; i++) {
      col[i] = buf[i][rowNdx];
    }
    return col;
  }

  public int getCols() {
    return buf[0].length;
  }

  public int getRows() {
    return buf.length;
  }

  Matrix subMatrix(int cols, int rows) {
    check(this.buf.length >= rows && this.buf[0].length >= cols,
          "subMatrix dimensions must be smaller");
    Matrix m = new Matrix(cols, rows);
    for (int i = 0; i < rows; i++) {
      System.arraycopy(buf[i], 0, m.buf[i], 0, cols);
    }
    return m;
  }

  static Matrix read(Reader reader) {
    BufferedReader r = new BufferedReader(reader);
    String line;
    String header = r.readLine();
    String[] colNames = header.split(",");
    double[][] tmp = new double[colNames.length][];
    while ((line = r.readLine()) != null) {
      String[] valsStr = line.split(",");
      tmp[i] = new double[valsStr.length];
      if (i > 0) {
        check(tmp[i].length == tmp[i - 1].length,
              "Input line %d length(%d) different from previous(%d)",
              i, tmp[i].length, tmp[i - 1].length);
      }
      for (int j = 0; j < valsStr.length; j++) {
        String valStr = valsStr[j];
        tmp[i][j] = Double.parseDouble(valStr);
      }
    }
    return new Matrix(tmp);
  }
}
