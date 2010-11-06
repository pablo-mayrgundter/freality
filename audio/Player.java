package audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import audio.SoundIO;

class Player {
  public static void main(final String [] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: Player [-Drecord=true] <file>");
      return;
    }
    final File f = new File(args[0]);
    if (Boolean.getBoolean("record")) {
      final SoundIO sio = new SoundIO();
      final FileOutputStream fos = new FileOutputStream(f);
      final Thread t = new Thread(new Runnable() {
          public void run() {
            try {
              sio.record(fos);
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
      fos.close();
    } else {
      final SoundIO sio = new SoundIO();
      final FileInputStream fis = new FileInputStream(f);
      sio.play(fis);
      sio.close();
      fis.close();
    }
  }
}