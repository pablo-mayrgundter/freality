package phys;

import util.Bits;

/**
 * The TimeSpace1DBinaryRing class represents a linear binary state
 * space that can vary over time.  The next state of the system can be
 * prepared and then set to be the current state.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class TimeSpace1DBinaryRing extends Space1DBinaryRing {

  Bits mNextStateSpace;

  public TimeSpace1DBinaryRing (final int extent) {
    super(extent);
    mNextStateSpace = new Bits(extent);
  }

  public final void setNext (final int ndx, final int val) {
    mNextStateSpace.set(ndx, val);
  }

  public final void nextTime () {
    Bits tmp = mStateSpace;
    mStateSpace = mNextStateSpace;
    mNextStateSpace = tmp;
    mNextStateSpace.clear();
  }
}

