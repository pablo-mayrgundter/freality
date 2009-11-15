package physics;

public class Space2D {

  final int [] mSpace;

  int mWidth, mHeight, mPos;

  public Space2D (final int width, final int height) {
    mSpace = new int[width * height];
    mWidth = width;
    mHeight = height;
  }

  public int pos (final int x, final int y) {
    return mSpace[mPos = y * mWidth + x];
  }

  public int up () {
    return mSpace[mPos - mWidth];
  }

  public int right () {
    return mSpace[mPos + 1];
  }

  public int down () {
    return mSpace[mPos + mWidth];
  }

  public int left () {
    return mSpace[mPos - 1];
  }

  public int get () {
    return mSpace[mPos];
  }

  public int get (final int x, final int y) {
    return mSpace[y * mWidth + x];
  }

  public void set (final int x, final int y, final int state) {
    mSpace[y * mWidth + x] = state;
  }
}
