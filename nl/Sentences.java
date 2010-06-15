package nl;

import java.io.InputStream;
import java.util.Iterator;

class Sentences implements Iterable<String> {
  final SentenceIterator itr;
  Sentences(final InputStream is) {
    itr = new SentenceIterator(is);
  }
  public Iterator<String> iterator() {
    return itr;
  }
}