package ai.atn;

import org.apache.commons.math3.linear.*;


class Test {


  public static void matrix() {
    // Assume we have a word embedding of size 3 for simplicity
    double[] wordEmbedding = {0.1, 0.2, 0.3};

    // And a 3x3 weight matrix for transforming the embedding into a query
    double[][] Wq = {
      {0.5, 0.6, 0.7},
      {0.8, 0.9, 1.0},
      {1.1, 1.2, 1.3}
    };

    RealVector embedding = new ArrayRealVector(wordEmbedding);
    RealMatrix queryWeightMatrix = new Array2DRowRealMatrix(Wq);

    // Perform the transformation
    RealVector query = queryWeightMatrix.preMultiply(embedding);

    // Print the resulting query vector
    System.out.println(query);
  }


  public static void main(String [] args) {
    if (args.length == 0) {
      System.err.println("Usage: java Test 'prompt tokens here'");
      return;
    }
    String [] tokens = args[0].split("\\s+");
    System.out.printf("Tokens\n");
    for (String t : tokens) {
      System.out.printf("%s\n", t);
    }
    matrix();
  }


  /**
   * Given an input sentence and an output sentence so far, generate
   * the next token of the output.
   */
  String transform(String [] input, String [] output) {
    return null;
  }


  /**
   * Compute the dot product of each query with each key, then use
   * softmax to find the largest.
   *
   * Basically one key will be selected.
   *
   * Queries and keys are of dimension Dk.  Values of dimension Dv.
   *
   * @param queries
   * @param keys
   * @param values
   */
  int [] scaledDotProductAttention(String [] queries, String [] keys, int [] values, int dk, int dv) {
    // dot product each key by the query
    for (int i = 0, n = queries.length; i < n; i++) {
      // q dot k
    }
    final float scalar = (float) Math.sqrt(dk);
    return null;
  }
}
