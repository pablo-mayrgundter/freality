import java.io.*;
import java.util.*;

/**
 * See http://www.cs.cmu.edu/~tom/mlbook.html
 * PGM code from http://www.thur.de/~Voland/pub/java/ .
 */
class Sunglasses {
  static float [] inputs = null;
  static int numInputs;
  public static void main(String [] args) {
    int numIterations = 10;
    // Read 4 files listing images, one each for left, right, straight, upward.

    ArrayList left = getPicsData("left.txt");
    ArrayList right = getPicsData("right.txt");
    ArrayList straight = getPicsData("straight.txt");
    ArrayList upward = getPicsData("up.txt");

    numInputs = ((PicData) left.get(0)).data.length;
    int numHidden = 3, numOuputs = 4;
    inputs = new float[numInputs];
    float learningRate = 0.3f;

    Backprop nn = new Backprop(numInputs, numOuputs, numHidden, learningRate);
    for (int i = 0; i < numIterations; i++) {
      train(nn, left, new float[]{0.9f,0.1f,0.1f,0.1f});
      train(nn, right, new float[]{0.1f,0.9f,0.1f,0.1f});
      train(nn, straight, new float[]{0.1f,0.1f,0.9f,0.1f});
      train(nn, upward, new float[]{0.1f,0.1f,0.1f,0.9f});
    }
  }

  static void train(Backprop nn, ArrayList picsData, float [] outputs) {

    for (int n = 0; n < picsData.size(); n++) { // Train on each pic.
      final PicData pic = (PicData) picsData.get(n);
      for (int i = 0; i < numInputs; i++) { // Set up inputs from pixel data.
	inputs[i] = pic.data[i] / pic.numColors;
	if(inputs[i] == 0.0) {
	  inputs[i] = 0.000001f;
        }
      }
      final float error = nn.learn(inputs, outputs);
      System.out.print("Error: ");
      System.out.print(error);
      System.out.print('\r');
    }
    System.out.println('\r');
  }

  static ArrayList getPicsData(String filename) {
    ArrayList<PicData> pics = new ArrayList<PicData>();
    try {
      LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(filename)));
      String line;
      while ((line = lnr.readLine()) != null) {
	PicData pic = new PicData(line);
	PGM.read(pic);
	pics.add(pic);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return pics;
  }
}


class PGM {
  /**
   * Read the pgmfile into a byte-array (oder two arrays).
   * @param <TT>p</TT> Helpobject, which gives the filename of the pgmfile.
   */
  public static void read(PicData p) throws IOException {

    FileInputStream fis = new FileInputStream(p.name);
    BufferedReader  r   = new BufferedReader(new InputStreamReader(fis));
    StreamTokenizer st  = new StreamTokenizer(r);
    int tok;
    st.commentChar('#'); st.eolIsSignificant(false);
    st.slashStarComments(false);  st.slashSlashComments(false);

    tok = st.nextToken();
    if (tok == st.TT_NUMBER || st.sval.compareTo("P5") != 0) {
      System.err.println("ERROR: " + p.name + " is not a PGM-file!");
      return;
    }

    st.nextToken(); p.x = (int)st.nval;         // width
    st.nextToken(); p.y = (int)st.nval;         // height
    st.nextToken(); p.numColors = (int)st.nval+1;  // max. color value +1

    int pixels = p.x * p.y;

    p.data = new float[pixels];
    int max = -1;
    for (int i = 0; i < pixels; i++) {
      final int pix = r.read(); // -128 flips the sign bit.
      if(pix > max)
	max = pix;
      p.data[i] = pix;
    }
    p.numColors = max + 1.0f;
  }
}


/**
 * This is a help-object used to give basic information about the
 * pictures.
 */
class PicData {

  public String name;     // name of picture or file
  public int    x;        // The width of the picture.
  public int    y;        // The height of the picture.
  public float numColors; // number of colors (usually 256).  Using a float since will be multing above.

  public float [] data; // raw picdata as 1dim array

  /**
   * The constructor. Remembers name and Typ.
   * @param <TT>name</TT> Filename of picture.
   */
  public PicData(String name) {
    this.name=name;
  }
}
