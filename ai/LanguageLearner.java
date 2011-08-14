package ai;

import ai.model.EntropyGraph;
import ai.model.WordGraph;
import ai.spatial.RandomLearner;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

class LanguageLearner extends RandomLearner<String> {

  final EntropyGraph<String> memory;
  final String delim;

  LanguageLearner() {
    this(new EntropyGraph<String>(), " ");
  }

  LanguageLearner(EntropyGraph<String> memory, String delim) {
    this.memory = memory;
    this.delim = delim;
  }

  public String learn(String s){
    listen(s);
    return speak();
  }

  protected void moveAway() {}
  protected void moveTowards() {}

  void listen(String line) {
    final String [] words = line.split(delim);
    for (int i = 0; i < words.length; i++) {
      final String word = words[i];
      if (word.length() == 0)
        continue;
      if (i == 0)
        memory.linkDirectional(word, word);
      else if (i == words.length - 1)
        memory.linkDirectional(word, word);
      else {
        memory.linkDirectional(word, words[i - 1]);
        memory.linkDirectional(word, words[i + 1]);
      }
    }
  }

  /**
   * Prints the maximum entropy path through the graph.
   */
  String speak() {
    return speak(memory.maxEntropyNode(), new HashSet<String>());
  }

  /**
   * Recursively prints the maximum entropy path from the given
   * node, without looping through nodes in the visted set.  When
   * this method recurses, the given word has been added to the
   * visted set.
   */
  String speak(String word, Set<String> visited) {
    if (word == null || visited.contains(word))
      return "";
    String out = word + "\n";
    visited.add(word);
    return out + speak(memory.highestEntropyNeighbor(word), visited);
  }

  public static void main(String [] args) throws IOException {
    final WordGraph wordNet = new WordGraph(); // hahah.
    final LanguageLearner l = new LanguageLearner(wordNet, "\\s+");
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
    String line;
    while ((line = lnr.readLine()) != null) {
      System.out.println(l.learn(line));
    }
  }
}
