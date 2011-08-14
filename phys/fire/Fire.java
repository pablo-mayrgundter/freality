package phys.fire;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;

class Fire extends JFrame implements Runnable {

  static final long serialVersionUID = -722656561849578647L;

  final int width;
  final int height;
  final Random r;
  final Map colors;

  Fire(boolean fullscreen) {
    super("Fire");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    if (fullscreen) {
      width = 1024;
      height = 768;
      setUndecorated(true);
      final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      final GraphicsConfiguration gc = device.getDefaultConfiguration();
      device.setFullScreenWindow(this);
      if (device.isDisplayChangeSupported())
        device.setDisplayMode(new DisplayMode(1024, 768, 32, 0));
    } else {
      width = 320;
      height = 240;
    }
    r = new Random();
    colors = new HashMap();
  }

  public void run() {
    setBackground(Color.BLACK);
    setSize(width + 10, height + 20);
    final Canvas c = new Canvas();
    c.setSize(width, height);
    c.setBackground(Color.BLACK);
    getContentPane().add(c);
    setVisible(true);

    final int imgHeight = 100;
    final Graphics g = c.getGraphics();
    final int [] pix = new int[width * imgHeight];
    final MemoryImageSource imageSource = new MemoryImageSource(width, imgHeight, pix, 0, width);
    imageSource.setAnimated(true);
    final Image image = createImage(imageSource);
    g.drawImage(image, 0, 0, c);
    int availImgHeight = 0;

    final int ALPHA_MASK = 255 << 24;
    final int RED_MASK = 255 << 16;
    final int BRIGHTEST = ALPHA_MASK;

    while (true) {
      if (availImgHeight < imgHeight)
        availImgHeight++;
      int ndx = 0;
      boolean bottom;
      for (int y = imgHeight - availImgHeight; y < imgHeight; y++) {
        bottom = y == imgHeight - 1;
        for (int x = 0; x < width; x++) {
          final int dice = r.nextInt(256);
          if (bottom)
            pix[ndx] = BRIGHTEST | (dice << 16);
          else {
            final int val = pix[ndx + width];
            pix[ndx] =
              dice < 20 ?
              ALPHA_MASK | (((val & RED_MASK) >> 4) & RED_MASK) :
              val;
          }
          ndx++;
        }
      }
      imageSource.newPixels(0, 0, width, availImgHeight);
      g.drawImage(image, 0, height - availImgHeight, c);
    }
  }

  public static void main(String [] args) {
    final Thread t = new Thread(new Fire(Boolean.getBoolean("fs")));
    t.setPriority(Thread.MAX_PRIORITY);
    t.start();
  }
}

