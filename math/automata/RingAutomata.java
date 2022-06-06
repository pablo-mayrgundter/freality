package math.automata;

import util.BitBuffer;


public interface RingAutomata extends Automata {
  void apply (final BitBuffer src, final BitBuffer dst);
}
