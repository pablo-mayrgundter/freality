package ai.atn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;


public  class Word2VecTest {
  public static void main(String [] args) throws Exception {
    if (args.length == 0) {
      System.err.println("Usage: java Test [sentences.txt]");
      return;
    }
    String filePath = new File(args[0]).getAbsolutePath();
    SentenceIterator iter = new BasicLineIterator(filePath);
    TokenizerFactory t = new DefaultTokenizerFactory();
    Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(10)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

    vec.fit();
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("A word?  I'll give more: ");
    String line;
    while ((line = r.readLine()) != null) {
      final double [] wordVec = vec.getWordVector(line);
      System.out.printf("Vector(len=%d): %s\n", wordVec.length, Arrays.toString(wordVec));
      System.out.println("Nearest: " + vec.wordsNearestSum(line, 10));
    }
  }
}
