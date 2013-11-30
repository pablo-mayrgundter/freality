package nl;

public class LexicalAttractionTest extends unit.TestCase {

  LexicalAttraction la = null;

  public void setUp() {
    la = new LexicalAttraction();
  }

  public void tearDown() {
    la = null;
  }

  public void test() {
    final String [] words = "a b c d".split("\\s+");
    la.reset(words);
    la.count();
    la.link();
  }
}
