package nl;

public class LexicalAttractionTest extends junit.framework.TestCase {

  LexicalAttraction la = null;

  public LexicalAttractionTest(final String name) {
    super(name);
  }

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

  public static void main(final String [] args) {
    junit.textui.TestRunner.run(LexicalAttractionTest.class);
  }
}