package math;

import gfx.FullScreenableFrame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;

class WolframAutomatons {

  static int clamp(int a, int b, int c) {
    return Math.max(a, Math.min(b, c));
  }

  static final int FROM = clamp(0, Integer.parseInt(System.getProperty("from", "0")), 255);
  static final int TO = clamp(FROM, Integer.parseInt(System.getProperty("to", "255")), 255);
  static final int SLEEP = Integer.parseInt(System.getProperty("sleep", "1000"));

  public static void main(String [] args){
    FullScreenableFrame f = new FullScreenableFrame();
    f.setVisible(true);
    Graphics g = f.getDrawGraphics();
    AutomatonDrawer drawer = new AutomatonDrawer(g, f.getWidth(), f.getHeight());

    for (int i = FROM; i <= TO; i++) {
      System.out.println("Drawing rule "+ i);
      f.setTitle("Rule #: "+ i);
      final BinaryAutomaton aut =
	new BinaryAutomaton(f.getWidth(),
			    new boolean[]{i / 128 % 2 == 1, // > 128 ?
					  i / 64 % 2 == 1, // > 64 ?
					  i / 32 % 2 == 1, // > 32 ?
					  i / 16 % 2 == 1, // > 16 ?
					  i / 8 % 2 == 1,  // > 8 ?
					  i / 4 % 2 == 1, // > 4 ?
					  i / 2 % 2 == 1, // > 2 ?
					  i % 2 == 1}); // > 1 ?
      
      drawer.reset();
      for (int j = 0; j < f.getHeight(); j++) {
	drawer.update(aut.next());
      }
      drawer.draw();
      util.Sleep.sleep(SLEEP);
    }
  }
}

class BinaryAutomaton {

  static final boolean RAND = Boolean.getBoolean("rand");
  static final long SEED = Integer.parseInt(System.getProperty("seed", "-1"));

  final boolean [] rule;
  boolean [] lastBits;

  BinaryAutomaton(int width, boolean [] rule) {
    if (rule.length != 8) {
      throw new IllegalArgumentException("Rule array must be length 8.");
    }
    this.rule = rule;
    lastBits = new boolean[width];
    if (RAND) {
      long seed;
      if (SEED != -1) {
        seed = SEED;
      } else {
        seed = System.currentTimeMillis();
      }
      Random r = new Random(seed);
      for (int i = 0; i < width; i++) {
        lastBits[i] = r.nextBoolean();
      }
    }
    lastBits[width/2] = true;
    /*
    System.out.println("[1,1,1][1,1,0][1,0,1][1,0,0][0,1,1][0,1,0][0,0,1][0,0,0]");
    System.out.println("  ["+(rule[0]?1:0)+"]   "+
		       " ["+(rule[1]?1:0)+"]   "+
		       " ["+(rule[2]?1:0)+"]   "+
		       " ["+(rule[3]?1:0)+"]   "+
		       " ["+(rule[4]?1:0)+"]   "+
		       " ["+(rule[5]?1:0)+"]   "+
		       " ["+(rule[6]?1:0)+"]   "+
		       " ["+(rule[7]?1:0)+"]   ");
    */
  }

  public boolean [] next() {
    final boolean [] newBits = new boolean[lastBits.length];
    boolean left;
    boolean center;
    boolean right;

    for (int i = 0; i < lastBits.length; i++){

      if (i == 0) {
	left = lastBits[0];
      } else {
	left = lastBits[i - 1];
      }

      center = lastBits[i];
      
      if (i == lastBits.length - 1) {
	right = lastBits[lastBits.length - 1];
      } else {
	right = lastBits[i + 1];
      }

      if (left && center && right) { // 7
	newBits[i] = rule[0];
      } else if (left && center && !right) { // 6
	newBits[i] = rule[1];
      } else if (left && !center && right) { // 5
	newBits[i] = rule[2];
      } else if (left && !center && !right) { // 4
	newBits[i] = rule[3];
      } else if (!left && center && right) { // 3
	newBits[i] = rule[4];
      } else if (!left && center && !right) { // 2
	newBits[i] = rule[5];
      } else if (!left && !center && right) { // 1
	newBits[i] = rule[6];
      } else {
	newBits[i] = rule[7];
      }
    }
    lastBits = newBits;
    return newBits;
  }
}

class AutomatonDrawer {

  final Graphics g;
  BufferedImage bufImg;
  int y;

  AutomatonDrawer(Graphics g, int width, int height){
    this.g = g;
    bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    reset();
  }

  void reset(){
    y = 0;
  }

  void update(boolean [] bits){
    for(int i = 0; i < bits.length; i++){
      bufImg.setRGB(i, y, bits[i] ? 0 : 255);
    }
    y++;
  }

  void draw(){
      g.drawImage(bufImg, 0, 0, null);
  }
}
