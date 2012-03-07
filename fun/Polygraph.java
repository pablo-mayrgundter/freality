package fun;

import gfx.FullScreenableFrame;
import java.awt.*;

class Polygraph extends FullScreenableFrame {

  static final long serialVersionUID = 3896239845753004157L;

  static final int SLEEP = Integer.parseInt(System.getProperty("sleep", "10"));

  Graphics2D g;

  Polygraph() {
    g = getDrawGraphics();
  }

  public Graphics2D getDrawGraphics() {
    return getDrawGraphics(Color.WHITE);
  }

  public void run() {
    g.setXORMode(Color.BLACK);
    int hipX = 400, hipY = 400, legLen = 200;
    int x1, y1, x2, y2, x1old = -1, y1old = -1, x2old = -1, y2old = -1, i = 0;
    int [] amp = new int[width - hipX + legLen];
    while (true) {
      if (i > 0) {
        g.drawLine(x1old, y1old, x2old, y2old);
      }
      x1 = hipX;
      y1 = hipY;
      x2 = hipX + legLen;
      y2 = (hipY + (int)(Math.sin((double)i / 100.0) * (double)legLen));
      g.drawLine(x1, y1, x2, y2);
      x1old = x1;
      y1old = y1;
      x2old = x2;
      y2old = y2;
      amp[i] = y2;
      g.setXORMode(Color.BLUE);
      drawAmp(x2, y1, amp, i);
      g.setXORMode(Color.BLACK);
      i++;
      util.Sleep.sleep(SLEEP);
    }
  }

  void drawAmp(int xOff, int yOff, int [] amp, int len) {
    for (int i = 0; i < amp.length; i++) {
      int x = xOff + len - i;
      int y = yOff + amp[i];
      g.drawLine(x, y, x, y);
    }
  }

  public static void main(String [] args) {
    new Polygraph().run();
  }
}