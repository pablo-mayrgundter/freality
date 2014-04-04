package fun;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import gfx.ColorMap;
import gfx.FullScreenableFrame;
import lang.nl.SentenceIterator;
import lang.nl.WordSizeFilter;

class Textamota {

  static final int SIZE = Integer.parseInt(System.getProperty("size", "10"));
  static final int XOFFSET = Integer.parseInt(System.getProperty("xoffset", "10"));
  static final int YOFFSET = Integer.parseInt(System.getProperty("yoffset", "10"));
  static final int MARGIN = Integer.parseInt(System.getProperty("margin", "10"));
  static final int MEMORY = Integer.parseInt(System.getProperty("memory", "10"));
  static final int SLEEP = Integer.parseInt(System.getProperty("sleep", "100"));
  static final int MIN_WORD_LEN = Integer.parseInt(System.getProperty("minlen", "0"));
  static final String WORD = System.getProperty("word", "8fvnsdf");

  class WordPos {
    int lineNo, pos;
    WordPos(final int lineNo, final int pos) {
      set(lineNo, pos);
    }
    void set(final int lineNo, final int pos) {
      this.lineNo = lineNo;
      this.pos = pos;
    }
  }

  final SentenceIterator itr;
  final Graphics2D g;
  final Color [] colors, grays;
  final int width, height;

  Textamota() throws Exception {
    itr = new SentenceIterator(new InputStreamReader(System.in, "UTF-16"));
    colors = ColorMap.linear(Color.BLUE, 128);
    grays = ColorMap.linear(MEMORY + 1);
    final FullScreenableFrame frame = new FullScreenableFrame();
    width = frame.getWidth();
    height = frame.getHeight();
    g = frame.getDrawGraphics();
  }

  public void run() throws Exception {
    int i, x = 0, y = YOFFSET;
    int freqMax = 1;
    final Map<String,Integer> freqs = new HashMap<String,Integer>();
    final Map<String,WordPos> lastRepeated = new TreeMap<String,WordPos>();
    int lineNo = 0, xoffset = XOFFSET, colWidth = 0;
    final WordSizeFilter sizeFilter = new WordSizeFilter(MIN_WORD_LEN);
    while (itr.hasNext()) {

      // Split sentence into words on any non-letter.
      String [] words = itr.next().toLowerCase().split("[^\\w]+");
      words = sizeFilter.process(words);

      if (words.length <= 1) {
        continue;
      }

      // Debug.
      String wordBuf = "";
      for (final String word : words)
        wordBuf += word + " ";
      System.out.println(wordBuf +".");

      // Compute frequencies and draw square of corresponding color at
      // an advancing cursor.
      x = xoffset;
      for (final String word : words) {
        Integer freq = freqs.get(word);
        if (freq == null)
          freq = 0;
        freq++;
        if (freq > freqMax)
          freqMax = freq;
        freqs.put(word, freq);
        g.setColor(colors[(int)((float)freq/(float)freqMax*127f)]);
        g.fillRect(x, y, SIZE, SIZE);
        x += SIZE;
      }

      // Draw a white cap square at end of sentence.
      g.setColor(Color.WHITE);
      g.fillRect(x, y, x + SIZE, SIZE);
      // Erase anything that was left on the rest of the line.
      g.setColor(Color.BLACK);
      g.fillRect(x + SIZE, y, width - x, SIZE);

      // Forget old words.
      final HashMap<String,WordPos> lastWords = new HashMap<String,WordPos>(lastRepeated);
      for (final String word : lastWords.keySet()) {
        final WordPos lastPos = lastRepeated.get(word);
        if (lastPos.lineNo < lineNo - MEMORY)
          lastRepeated.remove(word);
      }

      // Compare current words to words in memory.
      final Set<String> newRepeated = new TreeSet<String>();
      for (int cur = 0; cur < words.length; cur++) {
        final String word = words[cur];
        if (!lastRepeated.containsKey(word))
          continue;
        final WordPos lastPos = lastRepeated.get(word);
        final int distance = lineNo - lastPos.lineNo;
        final int strength = MEMORY - distance;
        g.setColor(grays[strength + 1]);
        g.drawLine(xoffset + lastPos.pos * SIZE, y - distance * SIZE, xoffset + cur * SIZE, y);
      }

      // Remember locations of current words.
      for (int cur = 0; cur < words.length; cur++) {
        final String word = words[cur];
        lastRepeated.put(word, new WordPos(lineNo, cur));
      }

      // Move graphics cursor to next row, or carry to first row and
      // next column.
      y += SIZE;
      if (y > height) {
        y = YOFFSET;
        xoffset += colWidth * SIZE + MARGIN;
        colWidth = 0;
      }
      if (words.length > colWidth)
        colWidth = words.length;
      lineNo++;

      util.Sleep.sleep(SLEEP);
    }

    // Debug.
    for (final String word : freqs.keySet()) {
      System.err.println(freqs.get(word) +","+ word);
    }
  }

  public static void main(final String [] args) throws Exception {
    new Textamota().run();
  }
}
