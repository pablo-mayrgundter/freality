package audio;

import java.util.Random;
import javax.sound.midi.*;

public class Synth {

  // SEt
  static {
    if (System.getProperty("java.awt.headless") == null)
      System.setProperty("java.awt.headless", "true");
  }

  final Random rand;
  final Sequencer sequencer;
  Track track;
  Sequence sequence;
  int tempo;
  long time;

  public Synth() {
    try {
      sequencer = MidiSystem.getSequencer();
      sequencer.open();
      // This mostly gets rid of a blurp sound at shutdown.
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
          public void run() {
            sequencer.close();
          }
        }));
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    rand = new Random();
  }

  public void recordSong() {
    recordSong(5000);
  }
  public void recordSong(final int tempo) {
    this.tempo = tempo;
    try {
      sequence = new Sequence(Sequence.PPQ, 10);
      track = sequence.createTrack();
    } catch (final InvalidMidiDataException e) { e.printStackTrace(); }
    time = 0;
  }

  public void randNote() {
    addNote(rand.nextInt(256), rand.nextInt(127), rand.nextInt(tempo));
  }
  public void addNote(final int note, final int velo, final int dura) {
    try {
      addNote(track, time, note, velo, dura);
    } catch (final InvalidMidiDataException e) {}
  }
  public void startNote(final int note) {
    try {
      noteEvent(track, time, note, rand.nextInt(100), true);
    } catch (final InvalidMidiDataException e) {}
  }
  public void endNote(final int note) {
    try {
      noteEvent(track, time, note, 0, false);
    } catch (final InvalidMidiDataException e) {}
  }
  public void endNote(final int note, final int t) {
    try {
      noteEvent(track, t, note, 0, false);
    } catch (final InvalidMidiDataException e) {}
  }
  public void timeForward(final int time) {
    this.time += time;
  }

  public void playSong() {
    try {
      sequencer.setSequence(sequence);
      sequencer.setTempoInMPQ(1000);
      sequencer.start();
      while (sequencer.isRunning())
        Thread.sleep(100);
      sequencer.stop();
    } catch (final Exception e) { e.printStackTrace(); }
  }

  public void close() {
    try {
      sequencer.close();
    } catch (final Exception e) { e.printStackTrace(); }
  }

  static void addNote(Track t, long time, int note, int velo, int dura) throws InvalidMidiDataException {
    noteEvent(t, time, note, velo, true);
    noteEvent(t, time + dura, note, velo, false);
  }

  static void noteEvent(final Track t, final long time, final int note, final int velo, final boolean on) throws InvalidMidiDataException {
    System.err.print(on ? "O" : "x");
    final ShortMessage msg = new ShortMessage();
    msg.setMessage(on ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF, 0, note, velo);
    t.add(new MidiEvent(msg, time + R.nextInt(1000)));
  }
  static final Random R = new Random();

  public static void main(String[] args) throws Exception {
    // Some lameness for the midi environment:
    //   1) Sound is a visual application by default.
    //   2) "Java Sound Event Dispatcher" is not a daemon thread, so have to exit explicitly.

    final Synth synth = new Synth();
    synth.recordSong();
    for (int i = 0; i < 100; i++) {
      synth.randNote();
      synth.timeForward(5000);
    }
    synth.playSong();

    System.exit(0);
  }
}
