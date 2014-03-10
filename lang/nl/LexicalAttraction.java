package lang.nl;

import java.util.*;

public class LexicalAttraction {

  class Link {
    final int a, b;
    Link(final int ndx1, final int ndx2) {
      if (ndx1 < ndx2) {
        a = ndx1; b = ndx2;
      } else {
        a = ndx2; b = ndx1;
      }
    }
    public boolean equals(Object o) {
      if (!(o instanceof Link))
        return false;
      final Link other = (Link) o;
      return other.a == a && other.b == b;
    }
  }

  final Lexicon lexicon;
  String [] words;
  LinkedHashSet<Link> [] leftLinks, rightLinks;
  Stack<Link> linkStack;
  Link [] minlink;

  LexicalAttraction() {
    lexicon = new Lexicon();
  }

  @SuppressWarnings(value="unchecked")
  void reset(final String [] words) {
    this.words = words;
    leftLinks = new LinkedHashSet[words.length];
    rightLinks = new LinkedHashSet[words.length];
    for (int i = 0; i < words.length; i++) {
      leftLinks[i] = new LinkedHashSet<Link>();
      rightLinks[i] = new LinkedHashSet<Link>();
    }
    linkStack = new Stack<Link>();
    minlink = new Link[words.length];
  }

  void count() {
    lexicon.add(words[0]);
    for (int i = 1; i < words.length; i++) {
      lexicon.add(words[i]);
      lexicon.addBigram(words[i-1], words[i]);
    }
  }

  float MI(final String s1, final String s2) {
    final float e1 = (float)math.Util.entropy(lexicon.getProbability(s1));
    final float e2 = (float)math.Util.entropy(lexicon.getProbability(s2));
    final float jointEntropy = (float)math.Util.entropy(lexicon.getBigramProbability(s1, s2));
    return e1 + e2 - jointEntropy;
  }

  float MI(final Link l) {
    return MI(words[l.a], words[l.b]);
  }

  float maxStackMI() {
    float maxMi = -1;
    for (final Link l : linkStack) {
      final float mi = MI(l);
      if (mi > maxMi)
        maxMi = mi;
    }
    return maxMi;
  }

  void unlinkStack() {
    for (final Link link : linkStack) {
      rightLinks[link.a].remove(link);
      leftLinks[link.b].remove(link);
    }
  }

  Link link(int a, int b) {
    final Link l = new Link(a, b);
    rightLinks[a].add(l);
    leftLinks[b].add(l);
    return l;
  }

  void link() {
    for (int j = 1; j < words.length; j++) {
      for (int i = j - 1; i >= 1; i--) {
        Link last = null;
        while (!linkStack.isEmpty())
          last = linkStack.pop();
        int lastNdx = last == null ? i + 1 : last.b;
        Link minLastLink = lastNdx >= words.length - 1 ? null : minlink[lastNdx + 1];
        minlink[i] = link(i, Math.min(lastNdx, minLastLink == null ? lastNdx : minLastLink.b));
        final float mi = MI(words[i], words[j]);
        if (mi > 0
            && mi > MI(minlink[i])
            && mi > maxStackMI()) {
          unlinkStack();
          linkStack.clear();
          minlink[i] = link(i, j);
        }
        for (final Link l : leftLinks[i])
          linkStack.push(l);
      }
    }
  }

  public String toString() {
    return String.format("Lexicon Stats: %s", this.lexicon);
  }

  public static void main(final String [] args) {
    final LexicalAttraction la = new LexicalAttraction();
    Viz v = new Viz();
    for (final String s : new Sentences(System.in)) {
      la.reset(s.split("\\s+"));
      la.count();
      la.link();
      v.update(la);
    }
    System.out.print(la);
  }
}
