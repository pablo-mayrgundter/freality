package lang.nl;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

class Lexicon {

  class Lexeme {
    final String gram;
    int count = 0;
    Lexeme(final String gram) {
      this.gram = gram;
    }
  }

  final Map<String,Lexeme> gramCounts;
  final Map<String,Lexeme> bigramCounts;
  int gramTotal, bigramTotal;

  Lexicon() {
    gramCounts = new HashMap<String,Lexeme>();
    bigramCounts = new HashMap<String,Lexeme>();
  }

  public void add(final String gram) {
    add(gramCounts, gram);
    gramTotal++;
  }

  public void addBigram(final String gramA, final String gramB) {
    add(bigramCounts, makeBigram(gramA, gramB));
    bigramTotal++;
  }

  public int getCount(final String gram) {
    return getCount(gramCounts, gram);
  }

  public int getBigramCount(final String gramA, final String gramB) {
    return getCount(bigramCounts, makeBigram(gramA, gramB));
  }

  public float getProbability(final String gram) {
    return (float)getCount(gramCounts, gram) / (float)gramTotal;
  }

  public float getBigramProbability(final String gramA, final String gramB) {
    return (float)getBigramCount(gramA, gramB) / (float)bigramTotal;
  }

  String makeBigram(final String gramA, final String gramB) {
    return gramA +" "+ gramB; // TODO(pmy): conditionally directional.
  }

  void add(final Map<String,Lexeme> counts, final String gram) {
    Lexeme lex = counts.get(gram);
    if (lex == null)
      counts.put(gram, lex = new Lexeme(gram));
    lex.count++;
  }

  int getCount(final Map<String,Lexeme> counts, final String gram) {
    Lexeme lex = counts.get(gram);
    if (lex == null)
      return 0;
    return lex.count;
  }

  public String toString() {
    return String.format("#grams: %d, #bigrams: %d", gramCounts.size(), bigramCounts.size());
  }
}
