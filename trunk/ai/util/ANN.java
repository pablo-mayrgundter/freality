package fmi;

/**
 * Artificial Neural Network.  This is a 2 layer backpropagation ANN
 * based on Tom Mitchell's book Machine Learning.<br>
 * 
 * http://www.cs.cmu.edu/~tom/mlbook.html
 *
 * @author pablo@freality.com
 */
public class ANN {

  int numInputs, numHidden, numOutputs, numSamples;


  // The network.
  double [][] layer1;
  double [][] layer2;

  // Temporary values in the network.
  double [] hiddenValues;
  double [] hiddenErrors;
  double [] guessErrors;

  // Guesses are kept for all training samples.
  double [] guessOutputs;

  double learningRate;

  static long randSeed = (new java.util.Date()).getTime();
  static java.util.Random r = new java.util.Random(randSeed);

  ANN(int numInputs, int numOutputs, int numHidden, double learningRate){
    this.numInputs = numInputs;
    this.numHidden = numHidden;
    this.numOutputs = numOutputs;
    this.learningRate = learningRate;

    hiddenValues = new double[numHidden];
    hiddenErrors = new double[numHidden];
    guessErrors = new double[numOutputs];

    layer1 = new double[numInputs][numHidden];
    layer2 = new double[numHidden][numOutputs];

    for(int i = 0; i < numInputs; i++) {
      for(int j = 0; j < numHidden; j++) {
	layer1[i][j] = r.nextDouble() * 0.05 * (r.nextBoolean() ? 1 : -1);
      }
    }

    for(int i = 0; i < numHidden; i++) {
      for(int j = 0; j < numOutputs; j++) {
	layer2[i][j] = r.nextDouble() * 0.05 * (r.nextBoolean() ? 1 : -1);
      }
    }
  }

  double backpropagate(double [] trainingInputs, double [] trainingOutputs, boolean training){
    
    guessOutputs = new double[numOutputs];

    double squaredErrorSum = 0;

    // Feed forward through Layer 1
    for(int j = 0; j < numHidden; j++) { // Compute each hidden value (Notice this is j, not i).
      hiddenValues[j] = 0;
      for(int i = 0; i < numInputs; i++) {// By multiplying inputs by layer 1 weights.
	hiddenValues[j] += trainingInputs[i] * layer1[i][j];
      }
      hiddenValues[j] = squash(hiddenValues[j]);
    }

    // Feed forward through Layer 2
    for(int k = 0; k < numOutputs; k++) { // Compute each hidden value (Notice this is k, not j).
      guessOutputs[k] = 0;
      for(int j = 0; j < numHidden; j++) {// By multiplying inputs by layer 1 weights.
	guessOutputs[k] += hiddenValues[j] * layer2[j][k];
      }
      guessOutputs[k] = squash(guessOutputs[k]);
    }

    // Measure output errors.
    double guessOutput;
    for(int k = 0; k < numOutputs; k++) {
      guessOutput = guessOutputs[k];
      guessErrors[k] = guessOutput * (1.0 - guessOutput) * (trainingOutputs[k] - guessOutput);
      squaredErrorSum += Math.pow(guessErrors[k], 2.0);
    }

    // Measure hidden errors.
    for(int j = 0; j < numHidden; j++) {
      hiddenErrors[j] = 0;
      for(int k = 0; k < numOutputs; k++) {
	hiddenErrors[j] += layer2[j][k] * guessErrors[k];
      }
      hiddenErrors[j] = hiddenValues[j] * (1.0 - hiddenValues[j]) * hiddenErrors[j];
    }

    if(training) {
      // Update the layer 2 weights.
      for(int j = 0; j < numHidden; j++){
	for(int k = 0; k < numOutputs; k++){
	  layer2[j][k] += learningRate * guessErrors[k] * layer2[j][k];
	}
      }

      // Update the layer 1 weights.
      for(int i = 0; i < numInputs; i++){
	for(int j = 0; j < numHidden; j++){
	  layer1[i][j] += learningRate * hiddenErrors[j] * layer1[i][j];
	}
      }
    }
    return squaredErrorSum;
  }

  /**
   * Squash a real into the range (0,1).  The Sigmoid function.
   */
  double squash(double val){
    final double ret = 1.0 / (1.0 + Math.pow(Math.E, -1.0 * val));
    if(ret >= 1.0) throw new IllegalStateException("sigmoid hit 1.0 on input: "+ val +", started with seed: "+ randSeed);
    return ret;
  }

  /**
   * Build an ANN to learn the identity function.  The training
   * samples are 8 bit fields, each with 8 bits, with one bit turned
   * on.  So, given:
   *
   *   1 0 0 0 0 0 0 0
   * 
   * the ANN must produce this on the outputs:
   * 
   *   1 0 0 0 0 0 0 0
   * 
   * Given:
   *
   *   0 1 0 0 0 0 0 0
   * 
   * the ANN must produce this on the outputs:
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
  public static void main(String [] args){

    int numInputs = 8, numHidden = 3, numOuputs = 8, numIterations = 5000;
    double learningRate = 0.05;

    ANN nn = new ANN(numInputs, numOuputs, numHidden, learningRate);

    // Make 2 training instances.
    double [] inputs = new double[numInputs];
    double [] outputs = new double[numOuputs];

    // Set up the identity function.
    for(int i = 0; i < numInputs; i++) {
      inputs[i] = 0.1;
      outputs[i] = 0.1;
    }

    // This makes numInputs training samples, one each for a single-bit flip.
    for(int n = 0; n < numIterations; n++) {
      for(int i = 0; i < numInputs; i++) {

	if(i != 0) {
	  inputs[i - 1] = 0.1;
	  outputs[i - 1] = 0.1;
	}
	inputs[i] = 0.9;
	outputs[i] = 0.9;
	final double error = nn.backpropagate(inputs, outputs, true);
	if(n % 200 == 0) {
	  System.out.print("Error: ");
	  System.out.print(error);
	  System.out.print('\r');
	}
      }
    }
    System.out.println();
  }
}
