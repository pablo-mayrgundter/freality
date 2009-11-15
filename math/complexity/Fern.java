package math.complexity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;

final class Fern {
  static final int MAX = Integer.parseInt(System.getProperty("max", "4"));
  final int mWidth, mHeight;
  final Graphics2D mGraphics;
  final BufferedImage mImg;

  Fern(final int width, final int height, final Graphics2D g) {
    mWidth = width;
    mHeight = height;
    mGraphics = g;
    mImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  void run() {

    float x = 0, y = 0;

    final Random rand = new Random();

    int maxX = mWidth / 2;
    int maxY = mHeight / 2;
    float curMaxX = 0;
    float curMaxY = 0;
    for (int i = 0; i < 100000; i++) {
      final int r = rand.nextInt(100);
      if (r <= 1) {
        x = 0;
        y = 0.16f * y;
      } else if (r <= 7) {
        x = 0.2f * x - 0.26f * y;
        y = 0.23f * x + 0.22f * y + 1.6f;
      } else if (r <= 14) {
        x = -0.15f * x + 0.28f * y;
        y = 0.26f * x + 0.24f * y + 0.44f;
      } else {
        x = 0.85f * x + 0.04f * y;
        y = -0.04f * x + 0.85f * y + 1.6f;
      }
      if (x > MAX || x < 0 || y > MAX || y < 0)
        continue;
      if (x > curMaxX) curMaxX = x;
      if (y > curMaxY) curMaxY = y;
      final int xc = squeeze(0, maxX + (x / 3.0) * mWidth / 2.0, mWidth - 1);
      final int yc = squeeze(0, mHeight - (maxY + (y / 10.0) * mHeight / 2.0), mHeight - 1);
      try {
        mImg.setRGB(xc + 20, yc + 20, Integer.MAX_VALUE);
      } catch (Exception e) {
        System.out.printf("failed on: %d, %d\n", xc, yc);
        break;
      }
    }
    System.out.printf("%.2f, %.2f\n", curMaxX, curMaxY);
    mGraphics.drawImage(mImg, null, 0, 0);
  }

  int squeeze(double a, double b, double c) {
    return (int) Math.max(a, Math.min(b, c));
  }

  public static void main(final String [] args) throws Exception {
    int width = 400, height = 400;
    final JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(width, height);
    f.setVisible(true);
    final Graphics2D g = (Graphics2D) f.getGraphics();
    Thread.sleep(1000);
    final Fern fern = new Fern(width, height, g);
    fern.run();
  }
}
