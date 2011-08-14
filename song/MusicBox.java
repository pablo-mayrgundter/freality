package song;

import java.io.*;
import java.util.Random;
import math.automata.Wolfram;
import math.automata.viz.Drawer;
import audio.Synth;
import gene.Pool;
import util.Bits;
import util.CLI;
import util.Flags;
import util.ObjectLoader;

final class MusicBox implements Runnable {

  /**
   * 256 notes split into 32 instruments of an octave each, but throw
   * away the first 32 notes, or 4 instruments to get rid of bad low
   * sounds, leaving 28.
   */
  static final int INSTRUMENTS = Flags.getInt("instruments", 28);
  static final int BEATS = Flags.getInt("beats", 16);
  static final int MEASURES = Flags.getInt("measures", 10);
  static final int LOWESTNOTE = Flags.getInt("lowestNote", 32);
  static final int TEMPO = Flags.getInt("tempo", 2000);
  static final boolean RANDINIT = Flags.getBool("randinit");
  static final String LOAD_FILE = Flags.get("loadFile", "song.obj");
  static final boolean LOAD = Flags.getBool("load", false);

  static final class Song implements java.io.Serializable {
    static final long serialVersionUID = -3733986784126345220L;
    byte [] startNotes;
    byte [][] rules;
    int measures;
    int beats;
    int startNotesLength;
  }

  class SongPool {

    Song mSong;

    SongPool() {

      if (LOAD) {
        mSong = ObjectLoader.load(LOAD_FILE);
      } else {
        mSong = new Song();
        mSong.rules = new byte[MEASURES][INSTRUMENTS];
        mSong.beats = BEATS;
        mSong.measures = MEASURES;
        for (int i = 0; i < mSong.measures; i++)
          for (int j = 0, n = mCur.getLength(); j < n; j++)
            mSong.rules[i][j] = randRule();
      }
    }

    /** Cross this song with next song, and replace next song. */
    public void bless() {
      for (int i = 0, n = numInstruments(); i < n; i++)
        if (mRand.nextBoolean())
          mSong.rules[mNextMeasure][i] = mSong.rules[mCurMeasure][i];
    }

    /** Mutate one of the rules in the current song. */
    public void curse() {
      mSong.rules[mCurMeasure][mRand.nextInt(numInstruments())] = randRule();
    }
  }

  final Synth mSynth;
  final Random mRand;
  final SongPool mSongPool;
  
  Bits mCur, mNext;
  int mCurMeasure, mNextMeasure, mLowestNote;

  MusicBox () {
    mCur = new Bits(INSTRUMENTS);
    mNext = new Bits(INSTRUMENTS);
    mSynth = new Synth();
    mRand = new Random();
    mLowestNote = LOWESTNOTE;
    mCurMeasure = 0;
    mSongPool = new SongPool();

    // Setup initial song input.
    if (RANDINIT)
      for (int j = 0, n = mCur.getLength(); j < n; j++)
        mCur.set(j, mRand.nextInt(2));
    else
      mCur.set(mSongPool.mSong.rules[0].length/2, 1);
  }

  private byte randRule() {
    return (byte) mRand.nextInt(256);
  }

  private int numInstruments() {
    return mSongPool.mSong.rules[0].length;
  }

  public void run() {
    while (true) {
      playSong();
      mCurMeasure = (mCurMeasure + 1) % mSongPool.mSong.rules.length;
      mNextMeasure = (mCurMeasure + 1) % mSongPool.mSong.rules.length;
    }
  }

  public void playSong () {
    mSynth.recordSong();
    Bits tmp;
    for (int y = 0; y < mSongPool.mSong.beats - 1; y++) {
      for (int j = 0, n = mCur.getLength(); j < n; j++) {
        Wolfram.apply(mCur, j, mNext, j, mSongPool.mSong.rules[mCurMeasure][j]);
        boolean on = mCur.get(j) == 1;
        boolean onNext = mNext.get(j) == 1;
        int bits = 4*mCur.get(j == 0 ? n - 1 : j-1) + 2*mCur.get(j) + mCur.get(j == n - 1 ? 0 : j+1);
        int note = 8 * j + mLowestNote + bits;
        if (on && y == 0) {
          if (onNext)
            mSynth.startNote(note);
          else
            System.err.print(".");
        } else if (!on && onNext)
          mSynth.startNote(note);
        else if (on && !onNext)
          mSynth.endNote(note);
        else if (on && y == mSongPool.mSong.beats - 2)
          mSynth.endNote(note);
        else
          System.err.print(".");
      }
      System.out.println(Drawer.drawBits(mCur));
      System.err.println();
      tmp = mCur; mCur = mNext; mNext = tmp;
      mNext.clear();
      mSynth.timeForward(TEMPO);
    }
    mSynth.playSong();
  }

  public static void main (final String [] args) {
    final MusicBox mb = new MusicBox();
    new Thread(mb).start();
    new Thread(new Runnable() {
        public void run() {
          while (true) {
            if (CLI.promptYesNo())
              mb.mSongPool.bless();
            else
              mb.mSongPool.curse();
            ObjectLoader.save(LOAD_FILE, mb.mSongPool.mSong);
          }
        }
      }).start();
  }
}
