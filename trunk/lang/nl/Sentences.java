package lang.nl;

import java.io.Reader;
import java.util.Iterator;

class Sentences implements Iterable<String> {
  final SentenceIterator itr;
  Sentences(final Reader r) {
    itr = new SentenceIterator(r);
  }
  public Iterator<String> iterator() {
    return itr;
  }
}
