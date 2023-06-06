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
  float [][] layer1;
  float [][] layer2;

  // Temporary values in the network.
  float [] hiddenValues;
  float [] hiddenErrors;
  float [] guessErrors;

  float learningRate;

  Backprop(int numInputs, int numOutputs, int numHidden, float learningRate) {
    this.numInputs = numInputs;
    this.numHidden = numHidden;
    this.numOutputs = numOutputs;
    this.learningRate = learningRate;

    hiddenValues = new float[numHidden];
    hiddenErrors = new float[numHidden];
    guessErrors = new float[numOutputs];

    layer1 = new float[numInputs][numHidden];
    layer2 = new float[numHidden][numOutputs];

    for (int i = 0; i < numInputs; i++) {
      for (int h = 0; h < numHidden; h++) {
	layer1[i][h] = (float) (r.nextDouble() * 2.0 - 1.0);
      }
    }

    for (int h = 0; h < numHidden; h++) {
      for (int o = 0; o < numOutputs; o++) {
	layer2[h][o] = (float) (r.nextDouble() * 2.0 - 1.0);
      }
    }
  }


  public String toString() {
    String s = "Network:\n";
    s += String.format("numInputs: %s\nnumHidden: %s\nnumOutputs: %s\n", numInputs, numHidden, numOutputs);
    s += "Seed: " + SEED + "\n";
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


  float [] predict(float [] inputs) {
    final float [] outputs = new float[numOutputs];

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


  float learn(float [] inputs, float [] trainingOutputs) {

    final float [] guessOutputs = predict(inputs);

    float squaredErrorSum = 0;

    // Measure output errors.
    float guessOutput;
    for (int o = 0; o < numOutputs; o++) {
      guessErrors[o] = trainingOutputs[o] - guessOutputs[o];
      squaredErrorSum += Math.pow(guessErrors[o], 2.0);
    }

    // Update the layer 2 weights.
    for (int h = 0; h < numHidden; h++) {
      for (int o = 0; o < numOutputs; o++) {
        final float gradient = guessErrors[o] * dsquash(guessOutputs[o]);
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
        final float gradient = hiddenErrors[h] * dsquash(hiddenValues[h]);
        layer1[i][h] += learningRate * gradient * inputs[i];
      }
    }

    return squaredErrorSum;
  }


  /**
   * Squash a real into the range (0,1).  The Sigmoid function.
   */
  float squash(float val) {
    final float ret = (float) (1.0 / (1.0 + Math.exp(-val)));
    if (ret > 1.0) {
      throw new IllegalStateException(String.format("sigmoid(%s): %s > 1", val, ret));
    }
    return ret;
  }


  float dsquash(float y) {
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

    if (Boolean.getBoolean("usage")) {
      System.out.println(
        "java <properties> Backprop\n\n" +
        "Properties:\n" +
        "  -Dusage=(true|false)\tPrint this message and exit.\n" +
        "  -DnumInputs=8\n" +
        "  -DnumHidden=4\n" +
        "  -DnumOutputs=8\n" +
        "  -DtargetError=0.05\n" +
        "  -Drate=0.05\n" +
        "  -Dseed=42\n" +
        "  -Dclamp=0.05\t\tClamp distance for rounding to 0 or 1 in outputs");
      return;
    }

    final int
      numInputs = Integer.getInteger("numInputs", 8),
      numHidden = Integer.getInteger("numHidden", 4),
      numOuputs = Integer.getInteger("numOutputs", 8);
    final float learningRate = Float.parseFloat(System.getProperty("rate", "0.05"));

    Backprop nn = new Backprop(numInputs, numOuputs, numHidden, learningRate);
    System.out.println("Initial network (random):\n" + nn);

    float [] inputs = new float[numInputs];
    float [] outputs = new float[numOuputs];


    System.out.printf("Initial prediction:\n%s -> %s\n\n",
                      dArrToS(inputs),
                      dArrToS(nn.predict(inputs)));

    // This makes numInputs training samples, one each for a single-bit flip.
    float error;
    int numIterations = 0;
    final float targetError = Float.parseFloat(System.getProperty("targetError", "0.05"));
    do {
      error = 0;
      for (int i = 0; i < numInputs; i++) {
        // Setup identity
        inputs[i] = outputs[i] = 1;

	error += nn.learn(inputs, outputs);

        // Clean up
        inputs[i] = outputs[i] = 0;
      }
      if (numIterations++ % 100000 == 0) {
        System.out.printf("numIterations: %d, error %s\r", numIterations, error / numInputs);
      }
    } while (error > targetError);
    System.out.printf("\n\nTrained network in %d iterations with final error %s:\n\n%s\n",
                      numIterations, error, nn);


    // Predict
    for (int i = 0; i < numInputs; i++) {
      inputs[i] = 1;
      System.out.printf("Trained prediction:\n%s -> %s\n",
                        dArrToS(inputs),
                        dArrToS(nn.predict(inputs)));
      inputs[i] = 0;
    }
  }


  static long SEED = Long.getLong("seed") == null ? (new java.util.Date()).getTime() : Long.getLong("seed");
  static java.util.Random r = new java.util.Random(SEED);
  static float CLAMP = Float.parseFloat(System.getProperty("clamp", "0.05"));

  static String dArrToS(float [] dArr) {
    String s = "[";
    for (int i = 0, n = dArr.length; i < n; i++) {
      s += dRound(dArr[i]);
      if (i < n - 1) {
        s += ", ";
      }
    }
    return s + "]";
  }


  static float dRound(float d) {
    if (d < CLAMP) {
      return 0.0f;
    }
    if (d > CLAMP) {
      return 1.0f;
    }
    return d;
  }
}
