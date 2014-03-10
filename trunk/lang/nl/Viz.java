package lang.nl;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;

class Viz {

  final Graphics2D g;
  final int width, height;
  final Insets insets;

  Viz() {
    JFrame f = new JFrame();
    f.setSize(640, 480);
    f.setVisible(true);
    try {
      Thread.currentThread().sleep(100);
    } catch (Exception e) {
    }
    width = f.getWidth();
    height = f.getHeight();
    insets = f.getInsets();
    g = (Graphics2D)f.getGraphics();
  }

  void update(final LexicalAttraction la) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    g.setColor(Color.WHITE);
    final Font font = g.getFont();
    final FontRenderContext frc = g.getFontRenderContext();
    final Rectangle2D [] bounds = new Rectangle2D[la.words.length];
    int x = insets.left + 20;
    int y = insets.top + 20;
    for (int i = 0; i < la.words.length; i++) {
      final String word = la.words[i];
      g.drawString(word, x, y);
      final Rectangle2D b = font.getStringBounds(word, frc);
      bounds[i] = b;
      int w = (int)b.getWidth(), h = (int)b.getHeight();
      g.drawRect(x, y - h, w, h);
      x += w + 10;
    }
  }
}
