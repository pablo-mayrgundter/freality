package audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import audio.SoundIO;

class Player {
  public static void main(final String [] args) throws Exception {
    if (args.length == 0) {
      help();
      return;
    }
    final File f = new File(args[0]);
    final SoundIO sio = new SoundIO();
    if (System.getProperty("op").equals("record")) {
      final Thread t = new Thread(new Runnable() {
          public void run() {
            try {
              sio.record(f);
            } catch (final Exception e) {
              e.printStackTrace();
              return;
            }
          }
        });
      System.out.println("Hit any key to start recording...");
      System.in.read();
      t.start();
      System.out.println("Recording...");
      System.out.println("Hit any key to stop recording...");
      System.in.read();
      System.out.println("Stopped.");
      sio.close();
    } else if (System.getProperty("op").equals("play")) {
      sio.play(f);
    } else if (System.getProperty("op").equals("display")) {
      sio.display(f);
    } else {
      help();
    }
    sio.close();
  }

  static void help () {
    System.out.println("Usage: Player [-Dop=<record|play|display>] <file>");
  }
}
