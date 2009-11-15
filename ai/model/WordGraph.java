package ai.model;

public class WordGraph extends EntropyGraph<String> {
    public void linkDirectional(String word1, String word2) {
        if (word1.length() == 0 || word2.length() == 0)
            throw new IllegalArgumentException("Empty word is not valid.");
        super.linkDirectional(word1, word2);
    }
}
