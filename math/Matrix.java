package math;

import static util.Check.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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

  public Matrix subMatrix(int cols, int rows) {
    check(this.buf.length >= rows && this.buf[0].length >= cols,
          "subMatrix dimensions must be smaller");
    Matrix m = new Matrix(cols, rows);
    for (int i = 0; i < rows; i++) {
      System.arraycopy(buf[i], 0, m.buf[i], 0, cols);
    }
    return m;
  }

  public static Matrix read(String s) {
    try {
      return read(new StringReader(s));
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid string: " + s);
    }
  }

  public static Matrix read(Reader reader) throws IOException {
    BufferedReader r = new BufferedReader(reader);
    String line;
    String header = r.readLine();
    String[] colNames = header.split(",");
    double[][] tmp = new double[colNames.length][];
    int lineNo = 0;
    while ((line = r.readLine()) != null) {
      String[] valsStr = line.split(",");
      tmp[lineNo] = new double[valsStr.length];
      if (lineNo > 0) {
        check(tmp[lineNo].length == tmp[lineNo - 1].length,
              "Input line %d length(%d) different from previous(%d)",
              lineNo, tmp[lineNo].length, tmp[lineNo - 1].length);
      }
      for (int j = 0; j < valsStr.length; j++) {
        String valStr = valsStr[j];
        tmp[lineNo][j] = Double.parseDouble(valStr);
      }
      lineNo++;
    }
    return new Matrix(tmp);
  }
}
