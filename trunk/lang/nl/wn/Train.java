package nl.wn;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;

class Train {
  public static void main(String [] args) throws IOException {

    // construct the URL to the Wordnet dictionary directory
    String wnhome = System.getenv("WNHOME");
    String path = wnhome + File.separator + "dict";
    URL url = new URL("file", null, path);
    // construct the dictionary object and open it
    IDictionary dict = new Dictionary(url);
    dict.open();

    // look up first sense of the word "dog"
    for (java.util.Iterator<IIndexWord> wordItr = dict.getIndexWordIterator(POS.NOUN);
         wordItr.hasNext();) {
      IIndexWord idxWord = wordItr.next();
      IWordID wordID = idxWord.getWordIDs().get(0);
      IWord word = dict.getWord(wordID);
      System.out.println("Id = " + wordID);
      System.out.println("Lemma = " + word.getLemma());
      System.out.println("Gloss = " + word.getSynset().getGloss());
    }
  }
}