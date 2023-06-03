package ai.atn;

import java.io.File;
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
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

    vec.fit();
    Collection<String> lst = vec.wordsNearestSum("day", 10);
    System.out.println(lst);
  }
}
