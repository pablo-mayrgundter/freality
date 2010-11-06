package audio;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.*;

public class SoundIO {

  DataLine l;

  public SoundIO() {
  }

  public void record(final OutputStream os) throws Exception {
    final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                    44100.0F, 16, 2, 4, 44100.0F, false);
    final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(info))
      throw new IllegalStateException("!AudioSystem.isLineSupported(info)");
    final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
    final AudioInputStream audioInputStream = new AudioInputStream(line);
    l = line;
    line.open(audioFormat);
    line.start();
    AudioSystem.write(audioInputStream, AudioFileFormat.Type.AU, os);
  }

  public void play(final InputStream is) throws Exception {
    final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
    final AudioFormat audioFormat = audioInputStream.getFormat();
    final Clip clip = AudioSystem.getClip();
    l = clip;
    clip.open(audioInputStream);
    clip.start();
    while (clip.isRunning()) {
      try {
        Thread.sleep(10);
      } catch(InterruptedException e) {
        return;
      }
    }
  }

  // http://jvalentino2.tripod.com/dft/index.html
  public void display(final byte [] abData) throws Exception {
    System.err.println("buf length: "+ abData.length);
    final ByteArrayInputStream is = new ByteArrayInputStream(abData);
    final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
    final AudioFormat audioFormat = audioInputStream.getFormat();

    final float sampleRate = audioFormat.getSampleRate();
    System.err.println("sample rate: "+ sampleRate);

    final float seconds = (float)audioInputStream.getFrameLength() / (float)audioFormat.getFrameRate();
    System.err.println("seconds: "+ seconds);

    final int samples = (int) (seconds * sampleRate) / 8;
    System.err.println("samples: "+ samples);

    final float sampleLength = seconds / (float)samples;
    System.err.println("sample length: "+ sampleLength);

    // Determine the original Endian encoding format.
    boolean isBigEndian = audioFormat.isBigEndian();

    // This array is the value of the signal at time i*h.
    final int [] x = new int[samples];

    // Convert each pair of byte values from the byte array to an Endian value.
    for (int i = 0; i < samples * 2; i += 2) {
      int b1 = abData[i];
      int b2 = abData[i + 1];
      if (b1 < 0) b1 += 0x100;
      if (b2 < 0) b2 += 0x100;

      int value;

      // Store the data based on the original Endian encoding format.
      if (!isBigEndian)
        value = (b1 << 8) + b2;
      else
        value = b1 + (b2 << 8);
      x[i/2] = value;
    }

    // Do the DFT for each value of x sub j and store as f sub j.
    double f[] = new double[samples/2];
    final javax.swing.JFrame frame = new javax.swing.JFrame();
    frame.setSize(800, 800);
    frame.setVisible(true);
    final java.awt.Graphics g = frame.getContentPane().getGraphics();
    for (int j = 0; j < samples/2; j++) {

      double firstSummation = 0;
      double secondSummation = 0;

      for (int k = 0; k < samples; k++) {
        double twoPInjk = ((2 * Math.PI) / samples) * (j * k);
        firstSummation +=  x[k] * Math.cos(twoPInjk);
        secondSummation += x[k] * Math.sin(twoPInjk);
      }

      f[j] = Math.abs( Math.sqrt(Math.pow(firstSummation,2) + 
                                 Math.pow(secondSummation,2)) );

      double amplitude = 2 * f[j]/samples;
      double frequency = (float)j * sampleLength / seconds * sampleRate / 100f;
      //        System.out.println("frequency = "+frequency+", amp = "+amplitude);
      // int xOff = (int)(800f * ((float)j / 82000f));
      int xOff = (int)amplitude;
      g.setColor(java.awt.Color.BLACK);
      g.drawLine(xOff, (int)frequency, xOff, (int)frequency);
      g.setColor(java.awt.Color.RED);
      g.drawLine(xOff, (int)amplitude, xOff, (int)amplitude);
    }
  }

  public void close() {
    if (l == null)
      throw new IllegalStateException("Must first record or play to close.");
    l.close();
  }
}
