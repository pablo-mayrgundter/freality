import java.io.File;
import java.io.RandomAccessFile;

class Test {
  public static void main(String [] args) {
    final String ROOT = "/home/pablo/public_html/helloWorld";
    final String [] files = new File(ROOT).list();
    for(int i = 0; i < files.length; i++) {
      File f = new File(files[i]);
      if (!(f.getName().toLowerCase().startsWith("hello"))) continue;
      try {
        final RandomAccessFile raf = new RandomAccessFile(new File(files[i]), "r");
      } catch(Exception e) {
        continue;
      }
    }
  }
}
