package nl;

public class LexiconTest extends unit.TestCase {

  Lexicon lexicon = null;

  public void setUp() {
    lexicon = new Lexicon();
  }

  public void tearDown() {
    lexicon = null;
  }

  public void test() {
    final String [] words = {"a", "b", "c", "d"};
    final float [] gramProbs = {1.0f, 0.5f, 0.333f, 0.25f};
    // 0th value just a placeholder.
    final float [] bigramProbs = {Float.NaN, 1.0f, 0.5f, 0.333f};

    lexicon.add(words[0]);
    assertEquals(1, lexicon.getCount(words[0]));
    assertEquals(gramProbs[0], lexicon.getProbability(words[0]));

    for (int i = 1; i < words.length; i++) {
      final String lastWord = words[i-1];
      final String word = words[i];

      assertEquals(0, lexicon.getCount(word));
      assertEquals(0f, lexicon.getProbability(word));
      lexicon.add(word);
      assertEquals(1, lexicon.getCount(word));
      assertEquals(gramProbs[i], lexicon.getProbability(word), 0.01f);

      assertEquals(0, lexicon.getBigramCount(lastWord, word));
      assertEquals(i == 1 ? Float.NaN : 0f, lexicon.getBigramProbability(lastWord, word));
      lexicon.addBigram(lastWord, word);
      assertEquals(1, lexicon.getBigramCount(lastWord, word));
      assertEquals(bigramProbs[i], lexicon.getBigramProbability(lastWord, word), 0.01f);
    }
  }
}
