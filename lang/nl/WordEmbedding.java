package lang.nl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see http://colah.github.io/posts/2014-07-NLP-RNNs-Representations/
 */
class WordEmbedding {

  final Map<String, double[]> wordVecs;
  final Set<String> seenWords;
  final List<String> seenWordsList;
  final int wordVectorLength;

  WordEmbedding() {
    this(10);
  }

  WordEmbedding(int wordVectorLength) {
    wordVecs = new HashMap<>();
    seenWords = new HashSet<>();
    seenWordsList = new ArrayList<>();
    this.wordVectorLength = wordVectorLength;
  }

  double[] createRandomWeights() {
    double[] weights = new double[wordVectorLength];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = Math.random();
    }
    return weights;
  }

  void learnPhrase(String[] phrase) {
    double phraseWeightSum = 0; // This is R in Colah's page.

    for (String word : phrase) {
      if (!seenWords.contains(word)) {
        seenWords.add(word);
      }

      double[] wordWeights = wordVecs.get(word);
      if (wordWeights == null) {
        wordVecs.put(word, wordWeights = createRandomWeights());
      }

      double wordWeightSum = 0; // This is W in Colah's page.
      for (double w : wordWeights) {
        wordWeightSum += w;
      }

      phraseWeightSum += wordWeightSum;
    }

    double phraseErr = Math.abs(1.0 - phraseWeightSum);
  }

  public static void main(String [] args) throws IOException {
    WordEmbedding we = new WordEmbedding();
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = r.readLine()) != null) {
      we.learnPhrase(line.split("\\s+"));
    }
  }
}