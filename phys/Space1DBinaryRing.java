package physics;

import util.Bits;

/**
 * The Space1DBinaryRing class represents a linear binary state space.
 *
 * @author Pablo Mayrgundter (pablo@freality.com)
 */
public class Space1DBinaryRing {

    Bits mStateSpace;

    public Space1DBinaryRing (final int extent) {
        mStateSpace = new Bits(extent);
    }

    public final int get (final int ndx) {
        return mStateSpace.get(ndx);
    }

    public final int getExtent () {
        return mStateSpace.getLength();
    }
}

