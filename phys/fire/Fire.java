package phys.fire;

import gfx.FullScreenableFrame;
import phys.Space2D;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Fire extends FullScreenableFrame implements Runnable {

  static final long serialVersionUID = -722656561849578647L;

  final Random r;
  final Map colors;

  Fire(boolean fullscreen) {
    super(400, 400, fullscreen);
    r = new Random();
    colors = new HashMap();
  }

  public void run() {
    Graphics2D g = getDrawGraphics(Color.BLACK);

    Space2D space = new Space2D(height);
    int [] pix = space.getBuffer();
    BufferedImage image =
      new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    int ALPHA_MASK = 255 << 24;
    int RED_MASK = 255 << 16;
    int BRIGHTEST = ALPHA_MASK;
    setBackground(Color.BLACK);
    g.setColor(Color.BLACK);
    g.drawRect(0, 0, width, height);

    while (true) {
      for (int bottom = height - 1, y = bottom; y >= 0; y--) {
        for (int x = 0; x < width; x++) {
          int dice = r.nextInt(256);
          int val;
          if (y == bottom) {
            val = BRIGHTEST | (dice << 16);
          } else {
            val = space.get(x, y + 1);
            val = dice < 20 ?
              ALPHA_MASK | (((val & RED_MASK) >> 4) & RED_MASK) :
              val;
          }
          space.set(val, x, y);
          image.setRGB(x, y, val);
        }
      }
      g.drawImage(image, 0, 0, this);
    }
  }

  public static void main(String [] args) {
    Thread t = new Thread(new Fire(Boolean.getBoolean("fs")));
    t.setPriority(Thread.MAX_PRIORITY);
    t.start();
  }
}

