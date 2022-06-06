package gfx;

import gfx.vt.VT100;
import util.Flags;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The TextGraphics class extends Java's Graphics2D class to display
 * graphics using VT100 terminal output.
 *
 * NOTE: under active development.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public final class TextGraphics extends Graphics2D {

  public static final List<Color> SUPPORTED_COLORS;
  static {
    System.setProperty("java.awt.headless", "true");
    SUPPORTED_COLORS = new ArrayList<Color>();
    for (final Color color : new Color[]{Color.BLACK,
                                         Color.RED,
                                         Color.GREEN,
                                         Color.YELLOW,
                                         Color.BLUE,
                                         Color.MAGENTA,
                                         Color.CYAN,
                                         Color.GRAY,
                                         Color.WHITE})
      SUPPORTED_COLORS.add(color);
  }

  static Flags flags = new Flags(TextGraphics.class);
  static final int WIDTH = flags.get("width", 80);
  static final int HEIGHT = flags.get("height", 24);

  final Map<Color,String> mFGColors;
  final Map<Color,String> mBGColors;
  int mWidth, mHeight;

  public TextGraphics() {
    this(WIDTH, HEIGHT);
  }

  public TextGraphics(final int width, final int height) {
    mWidth = width;
    mHeight = height;
    mFGColors = new HashMap<Color,String>();
    mBGColors = new HashMap<Color,String>();
    int x = 0;
    for (final Color c : SUPPORTED_COLORS) {
      mFGColors.put(c, "\033[0;"+(30 + x)+"m");
      mBGColors.put(c, "\033[0;"+(40 + x)+"m");
      x++;
    }
    System.out.print(VT100.CLEAR_SCREEN);
    final TextGraphics tg = this;
    Runtime.getRuntime().addShutdownHook(new Thread(){
        public void run() {
          tg.dispose();
        }
      });
  }

  public static void main(String [] args) {
    int x1 = Integer.parseInt(args[0]), y1 = Integer.parseInt(args[1]),
      x2 = Integer.parseInt(args[2]), y2 = Integer.parseInt(args[3]);
    TextGraphics g = new TextGraphics();
    java.util.Random r = new java.util.Random();
    g.setBackground(SUPPORTED_COLORS.get(r.nextInt(SUPPORTED_COLORS.size())));
    g.drawLine(x1, y1, x2, y2);
    g.drawPixel(10, 1);
    g.dispose();
  }

  void p(final String s) {
    System.out.print(s);
  }

  public void clear() {
    System.out.print(VT100.CLEAR_SCREEN);
  }

  public void drawPixel(final int x, final int y) {
    p(VT100.cursorForce(y, x) + " ");
  }

  public void drawLine(final int line, final String s) {
    p(VT100.cursorForce(line, 0) + s);
  }

  public void scroll() {
    setBackground(java.awt.Color.BLACK);
    System.out.println();
    setBackground(java.awt.Color.WHITE);
  }

  // API methods.

  /** Sets the values of an arbitrary number of preferences for the rendering algorithms. */
  public void addRenderingHints(Map<?,?> hints) { throw new UnsupportedOperationException(); }

  /** Clears the specified rectangle by filling it with the background color of the current drawing surface. */
  public void clearRect(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Intersects the current Clip with the interior of the specified Shape and sets the Clip to the resulting intersection. */
  public void clip(Shape s) { throw new UnsupportedOperationException(); }

  /** Intersects the current clip with the specified rectangle. */
  public void clipRect(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Copies an area of the component by a distance specified by dx and dy. */
  public void copyArea(int x, int y, int width, int height, int dx, int dy) { throw new UnsupportedOperationException(); }

  /** Creates a new Graphics object that is a copy of this Graphics object. */
  public Graphics create() { throw new UnsupportedOperationException(); }

  /** Disposes of this graphics context and releases any system resources that it is using. */
  public void dispose() {
    setColor(Color.WHITE);
    setBackground(Color.BLACK);
    p(VT100.CURSOR_HOME);
  }

  public void finalize () {
    dispose();
  }

  /** Strokes the outline of a Shape using the settings of the current Graphics2D context. */
  public void draw(Shape s) { throw new UnsupportedOperationException(); }

  /** Draws the outline of a circular or elliptical arc covering the specified rectangle. */
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) { throw new UnsupportedOperationException(); }

  /** Renders the text of the specified GlyphVector using the Graphics2D context's rendering attributes. */
  public void drawGlyphVector(GlyphVector g, float x, float y) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified image as is currently available. */
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified image as is currently available. */
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified image as has already been scaled to fit inside the specified rectangle. */
  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified image as has already been scaled to fit inside the specified rectangle. */
  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified area of the specified image as is currently available, scaling it on the fly to fit inside the specified area of the destination drawable surface. */
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Draws as much of the specified area of the specified image as is currently available, scaling it on the fly to fit inside the specified area of the destination drawable surface. */
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) { throw new UnsupportedOperationException(); }

  /** Renders a BufferedImage that is filtered with a BufferedImageOp. */
  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) { throw new UnsupportedOperationException(); }

  /** Renders an image, applying a transform from image space into user space before drawing. */
  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) { throw new UnsupportedOperationException(); }

  /** Draws a line, using the current color, between the points (x1, y1) and (x2, y2) in this graphics context's coordinate system. */
  public void drawLine(final int x1, final int y1, final int x2, final int y2) {
    float dx = x2 - x1, dy = y2 - y1, x = x1, y = y1;
    float xStep = dx/dy, yStep = dy/dx;
    int absDx = Math.abs((int)dx), absDy = Math.abs((int)dy);
    int numIts;
    if (absDx > absDy)
      numIts = absDx;
    else
      numIts = absDy;
    //System.out.printf("dx: %.2f, dy: %.2f, numIts: %d, xStep: %.2f, yStep: %.2f\n", dx, dy, numIts, xStep, yStep);
    //setBackground(Color.WHITE);
    for (int i = 0; i < numIts; i++) {
      drawPixel((int)x, (int)y);
      x += xStep;
      y += yStep;
    }
  }

  /** Draws the outline of an oval. */
  public void drawOval(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Draws a closed polygon defined by arrays of x and y coordinates. */
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) { throw new UnsupportedOperationException(); }

  /** Draws a sequence of connected lines defined by arrays of x and y coordinates. */
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) { throw new UnsupportedOperationException(); }

  /** Renders a RenderableImage, applying a transform from image space into user space before drawing. */
  public void drawRenderableImage(RenderableImage img, AffineTransform xform) { throw new UnsupportedOperationException(); }

  /** Renders a RenderedImage, applying a transform from image space into user space before drawing. */
  public void drawRenderedImage(RenderedImage img, AffineTransform xform) { throw new UnsupportedOperationException(); }

  /** Draws an outlined round-cornered rectangle using this graphics context's current color. */
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { throw new UnsupportedOperationException(); }

  /** Renders the text of the specified iterator, using the Graphics2D context's current Paint. */
  public void drawString(AttributedCharacterIterator iterator, float x, float y) { throw new UnsupportedOperationException(); }

  /** Renders the text of the specified iterator, using the Graphics2D context's current Paint. */
  public void drawString(AttributedCharacterIterator iterator, int x, int y) { throw new UnsupportedOperationException(); }

  /** Renders the text specified by the specified String, using the current text attribute state in the Graphics2D context. */
  public void drawString(String s, float x, float y) { throw new UnsupportedOperationException(); }

  /** Renders the text of the specified String, using the current text attribute state in the Graphics2D context. */
  public void drawString(String str, int x, int y) { throw new UnsupportedOperationException(); }

  /** Fills the interior of a Shape using the settings of the Graphics2D context. */
  public void fill(Shape s) { throw new UnsupportedOperationException(); }

  /** Fills a circular or elliptical arc covering the specified rectangle. */
  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) { throw new UnsupportedOperationException(); }

  /** Fills an oval bounded by the specified rectangle with the current color. */
  public void fillOval(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Fills a closed polygon defined by arrays of x and y coordinates. */
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) { throw new UnsupportedOperationException(); }

  /** Fills the specified rectangle. */
  public void fillRect(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Fills the specified rounded corner rectangle with the current color. */
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { throw new UnsupportedOperationException(); }

  /** Returns the background color used for clearing a region. */
  public Color getBackground() { throw new UnsupportedOperationException(); }

  /** Gets the current clipping area. */
  public Shape getClip() { throw new UnsupportedOperationException(); }

  /** Returns the bounding rectangle of the current clipping area. */
  public Rectangle getClipBounds() { throw new UnsupportedOperationException(); }

  /** Gets this graphics context's current color. */
  public Color getColor() { throw new UnsupportedOperationException(); }

  /** Returns the current Composite in the Graphics2D context. */
  public Composite getComposite() { throw new UnsupportedOperationException(); }

  /** Returns the device configuration associated with this Graphics2D. */
  public GraphicsConfiguration getDeviceConfiguration() { throw new UnsupportedOperationException(); }

  /** Gets the current font. */
  public Font getFont() { throw new UnsupportedOperationException(); }

  /** Gets the font metrics for the specified font. */
  public FontMetrics getFontMetrics(Font f) { throw new UnsupportedOperationException(); }

  /** Get the rendering context of the Font within this Graphics2D context. */
  public FontRenderContext getFontRenderContext() { throw new UnsupportedOperationException(); }

  /** Returns the current Paint of the Graphics2D context. */
  public Paint getPaint() { throw new UnsupportedOperationException(); }

  /** Returns the value of a single preference for the rendering algorithms. */
  public Object getRenderingHint(RenderingHints.Key hintKey) { throw new UnsupportedOperationException(); }

  /** Gets the preferences for the rendering algorithms. */
  public RenderingHints getRenderingHints() { throw new UnsupportedOperationException(); }

  /** Returns the current Stroke in the Graphics2D context. */
  public Stroke getStroke() { throw new UnsupportedOperationException(); }

  /** Returns a copy of the current Transform in the Graphics2D context. */
  public AffineTransform getTransform() { throw new UnsupportedOperationException(); }

  /** Checks whether or not the specified Shape intersects the specified Rectangle, which is in device space. */
  public boolean hit(Rectangle rect, Shape s, boolean onStroke) { throw new UnsupportedOperationException(); }

  /** Concatenates the current Graphics2D Transform with a rotation transform. */
  public void rotate(double theta) { throw new UnsupportedOperationException(); }

  /** Concatenates the current Graphics2D Transform with a translated rotation transform. */
  public void rotate(double theta, double x, double y) { throw new UnsupportedOperationException(); }

  /** Concatenates the current Graphics2D Transform with a scaling transformation Subsequent rendering is resized according to the specified scaling factors relative to the previous scaling. */
  public void scale(double sx, double sy) { throw new UnsupportedOperationException(); }

  /** Sets the background color for the Graphics2D context. */
  public void setBackground(final Color color) {
    final String code = mBGColors.get(color);
    if (code == null)
      throw new IllegalArgumentException("Unsupported color: "+ color);
    p(code);
  }

  /** Sets the current clip to the rectangle specified by the given coordinates. */
  public void setClip(int x, int y, int width, int height) { throw new UnsupportedOperationException(); }

  /** Sets the current clipping area to an arbitrary clip shape. */
  public void setClip(Shape clip) { throw new UnsupportedOperationException(); }

  /** Sets this graphics context's current color to the specified color. */
  public void setColor(final Color color) {
    final String code = mFGColors.get(color);
    if (code == null)
      throw new IllegalArgumentException("Unsupported color: "+ color);
    p(code);
  }

  /** Sets the Composite for the Graphics2D context. */
  public void setComposite(Composite comp) { throw new UnsupportedOperationException(); }

  /** Sets this graphics context's font to the specified font. */
  public void setFont(Font font) { throw new UnsupportedOperationException(); }

  /** Sets the Paint attribute for the Graphics2D context. */
  public void setPaint(Paint paint) { throw new UnsupportedOperationException(); }

  /** Sets the paint mode of this graphics context to overwrite the destination with this graphics context's current color. */
  public void setPaintMode() { throw new UnsupportedOperationException(); }

  /** Sets the value of a single preference for the rendering algorithms. */
  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) { throw new UnsupportedOperationException(); }

  /** Replaces the values of all preferences for the rendering algorithms with the specified hints. */
  public void setRenderingHints(Map<?,?> hints) { throw new UnsupportedOperationException(); }

  /** Sets the Stroke for the Graphics2D context. */
  public void setStroke(Stroke s) { throw new UnsupportedOperationException(); }

  /** Overwrites the Transform in the Graphics2D context. */
  public void setTransform(AffineTransform Tx) { throw new UnsupportedOperationException(); }

  /** Sets the paint mode of this graphics context to alternate between this graphics context's current color and the new specified color. */
  public void setXORMode(Color c1) { throw new UnsupportedOperationException(); }

  /** Concatenates the current Graphics2D Transform with a shearing transform. */
  public void shear(double shx, double shy) { throw new UnsupportedOperationException(); }

  /** Composes an AffineTransform object with the Transform in this Graphics2D according to the rule last-specified-first-applied. */
  public void transform(AffineTransform Tx) { throw new UnsupportedOperationException(); }

  /** Concatenates the current Graphics2D Transform with a translation transform. */
  public void translate(double tx, double ty) { throw new UnsupportedOperationException(); }

  /** Translates the origin of the Graphics2D context to the point (x, y) in the current coordinate system. */
  public void translate(int x, int y) { throw new UnsupportedOperationException(); }
}
