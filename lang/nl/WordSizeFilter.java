package lang.nl;

import java.util.ArrayList;
import java.util.List;

public class WordSizeFilter implements WordProcessor {

  final int minSize;

  public WordSizeFilter(final int minSize) {
    this.minSize = minSize;
  }

  public String [] process(final String [] words) {
    final List<String> bigWords = new ArrayList<String>();
    for (final String word : words) {
      if (word.length() >= minSize)
        bigWords.add(word);
    }
    return bigWords.toArray(new String[bigWords.size()]);
  }
}
