package ai;

import ai.model.EntropyGraph;
import ai.model.WordGraph;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

class LanguageLearner {

    final EntropyGraph<String> mAssocGraph;
    final String mDelim;

    LanguageLearner(EntropyGraph<String> assocGraph, String delim) {
        mAssocGraph = assocGraph;
        mDelim = delim;
    }

    void listen(LineNumberReader lnr) throws IOException {
        String line;
        while ((line = lnr.readLine()) != null) {
            final String [] words = line.split(mDelim);
            for (int i = 0; i < words.length; i++) {
                final String word = words[i];
                if (word.length() == 0)
                    continue;
                if (i == 0)
                    mAssocGraph.linkDirectional(word, word);
                else if (i == words.length - 1)
                    mAssocGraph.linkDirectional(word, word);
                else {
                    mAssocGraph.linkDirectional(word, words[i - 1]);
                    mAssocGraph.linkDirectional(word, words[i + 1]);
                }
            }
        }
    }

    /**
     * Prints the maximum entropy path through the graph.
     */
    void speak(PrintStream ps) {
        speak(mAssocGraph.maxEntropyNode(), ps, new HashSet<String>());
    }

    /**
     * Recursively prints the maximum entropy path from the given
     * node, without looping through nodes in the visted set.  When
     * this method recurses, the given word has been added to the
     * visted set.
     */
    void speak(String word, PrintStream ps, Set<String> visited) {
        if (word == null || visited.contains(word))
            return;
        ps.print(word + " ");
        visited.add(word);
        speak(mAssocGraph.highestEntropyNeighbor(word), ps, visited);
    }

    public static void main(String [] args) throws IOException {
        final WordGraph wordNet = new WordGraph(); // hahah.
        final LanguageLearner l = new LanguageLearner(wordNet, "\\s+");
        l.listen(new LineNumberReader(new InputStreamReader(System.in)));
        l.speak(System.out);
    }
}
