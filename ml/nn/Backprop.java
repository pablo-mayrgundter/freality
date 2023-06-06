/**
 * This is a 2 layer back-propagation neural-network design based on
 * Tom Mitchell's book Machine Learning.
 * <p>
 * http://www.cs.cmu.edu/~tom/mlbook.html
 *
 * @author Pablo Mayrgundter
 */
public class Backprop {

  int numInputs, numHidden, numOutputs, numSamples;

  // The network.
  double [][] layer1;
  double [][] layer2;

  // Temporary values in the network.
  double [] hiddenValues;
  double [] hiddenErrors;
  double [] guessErrors;

  double learningRate;

  static long randSeed = Long.getLong("seed") == null ? (new java.util.Date()).getTime() : Long.getLong("seed");
  static {
    System.out.println("Seed: " + randSeed);
  }
  static java.util.Random r = new java.util.Random(randSeed);


  static String dArrToS(double [] dArr) {
    String s = "[";
    for (int i = 0, n = dArr.length; i < n; i++) {
      s += dRound(dArr[i]);
      if (i < n - 1) {
        s += ", ";
      }
    }
    return s + "]";
  }


  static double dRound(double d) {
    if (d < 0.05) {
      return 0.0;
    }
    if (d > 0.95) {
      return 1.0;
    }
    return d;
  }


  Backprop(int numInputs, int numOutputs, int numHidden, double learningRate) {
    this.numInputs = numInputs;
    this.numHidden = numHidden;
    this.numOutputs = numOutputs;
    this.learningRate = learningRate;

    hiddenValues = new double[numHidden];
    hiddenErrors = new double[numHidden];
    guessErrors = new double[numOutputs];

    layer1 = new double[numInputs][numHidden];
    layer2 = new double[numHidden][numOutputs];

    for (int i = 0; i < numInputs; i++) {
      for (int h = 0; h < numHidden; h++) {
	layer1[i][h] = r.nextDouble() * 2.0 - 1.0;
      }
    }

    for (int h = 0; h < numHidden; h++) {
      for (int o = 0; o < numOutputs; o++) {
	layer2[h][o] = r.nextDouble() * 2.0 - 1.0;
      }
    }
  }


  public String toString() {
    String s = "";
    for (int i = 0; i < numInputs; i++) {
      s += java.util.Arrays.toString(layer1[i]) + "\n";
    }

    for (int o = 0; o < numOutputs; o++) {
      s += "[";
      for (int h = 0; h < numHidden; h++) {
        s += layer2[h][o] + (h < numHidden - 1 ? ", " : "");
      }
      s += "]\n";
    }

    return s;
  }


  double [] predict(double [] inputs) {
    final double [] outputs = new double[numOutputs];

    // Feed forward through Layer 1
    for (int h = 0; h < numHidden; h++) { // Compute each hidden value (Notice this is j, not i).
      hiddenValues[h] = 0;
      for (int i = 0; i < numInputs; i++) {// By multiplying inputs by layer 1 weights.
	hiddenValues[h] += inputs[i] * layer1[i][h];
      }
      hiddenValues[h] = squash(hiddenValues[h]);
    }

    // Feed forward through Layer 2
    for (int o = 0; o < numOutputs; o++) { // Compute each hidden value (Notice this is k, not j).
      outputs[o] = 0;
      for (int h = 0; h < numHidden; h++) {// By multiplying inputs by layer 1 weights.
	outputs[o] += hiddenValues[h] * layer2[h][o];
      }
      outputs[o] = squash(outputs[o]);
    }

    return outputs;
  }


  double learn(double [] inputs, double [] trainingOutputs) {

    final double [] guessOutputs = predict(inputs);

    double squaredErrorSum = 0;

    // Measure output errors.
    double guessOutput;
    for (int o = 0; o < numOutputs; o++) {
      guessErrors[o] = trainingOutputs[o] - guessOutputs[o];
      squaredErrorSum += Math.pow(guessErrors[o], 2.0);
    }

    // Update the layer 2 weights.
    for (int h = 0; h < numHidden; h++) {
      for (int o = 0; o < numOutputs; o++) {
        final double gradient = guessErrors[o] * dsquash(guessOutputs[o]);
        layer2[h][o] += learningRate * gradient * hiddenValues[h];
      }
    }

    // Measure hidden errors.
    for (int h = 0; h < numHidden; h++) {
      hiddenErrors[h] = 0;
      for (int o = 0; o < numOutputs; o++) {
	hiddenErrors[h] += layer2[h][o] * guessErrors[o];
      }
    }

    // Update the layer 1 weights.
    for (int i = 0; i < numInputs; i++) {
      for (int h = 0; h < numHidden; h++) {
        final double gradient = hiddenErrors[h] * dsquash(hiddenValues[h]);
        layer1[i][h] += learningRate * gradient * inputs[i];
      }
    }

    return squaredErrorSum;
  }


  /**
   * Squash a real into the range (0,1).  The Sigmoid function.
   */
  double squash(double val) {
    final double ret = 1.0 / (1.0 + Math.exp(-val));
    if (ret > 1.0) {
      throw new IllegalStateException(String.format("sigmoid(%s): %s > 1", val, ret));
    }
    return ret;
  }


  double dsquash(double y) {
    // derivative of sigmoid function
    return y * (1 - y);
  }


  /**
   * Build an NeuralNetwork to learn the identity function.  The training
   * samples are 8 bit fields, each with 8 bits, with one bit turned
   * on.  So, given:
   *
   *   1 0 0 0 0 0 0 0
   *
   * the NeuralNetwork must produce this on the outputs:
   *
   *   1 0 0 0 0 0 0 0
   *
   * Given:
   *
   *   0 1 0 0 0 0 0 0
   *
   * the NeuralNetwork must produce this on the outputs:
   *
   *   0 1 0 0 0 0 0 0
   *
   * and so on.  This is exactly what is done below.  Because the
   * squashing function can't yield 0 or 1 as outputs exactly, 0 is
   * represented by 0.1 and 1 is represented by 0.9.
   *
   * The program outputs the error rates for successive training runs.
   * This should decrease with each run.
   */
  public static void main(String [] args) {

    int numInputs = 8, numHidden = 5, numOuputs = 8, numIterations = 50000;
    double learningRate = 0.05;

    Backprop nn = new Backprop(numInputs, numOuputs, numHidden, learningRate);
    System.out.println("Initial network (random):\n" + nn);

    double [] inputs = new double[numInputs];
    double [] outputs = new double[numOuputs];


    System.out.printf("Initial prediction:\n%s -> %s\n\n",
                      dArrToS(inputs),
                      dArrToS(nn.predict(inputs)));

    // This makes numInputs training samples, one each for a single-bit flip.
    double error;
    do {
      error = 0;
      for (int i = 0; i < numInputs; i++) {
        // Setup identity
        inputs[i] = outputs[i] = 1;

	error += nn.learn(inputs, outputs);

        // Clean up
        inputs[i] = outputs[i] = 0;
        System.out.printf("Error %s\r", error);
      }
    } while (error > 0.01);
    System.out.println("\n\nTrained network:\n" + nn);


    // Predict
    for (int i = 0; i < numInputs; i++) {
      inputs[i] = 1;
      System.out.printf("Trained prediction:\n%s -> %s\n",
                        dArrToS(inputs),
                        dArrToS(nn.predict(inputs)));
      inputs[i] = 0;
    }
  }
}
