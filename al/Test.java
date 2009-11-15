package al;

import gfx.vt.VT100;

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Test the long term occurrence of altruistic behavior in a
 * population of creatures.  The test is parameterized by population
 * size, number of generations, altruism rate, predation rate and a
 * random number generator seed.
 *
 * The sexual evolution algorithm:
 *
 * A species is composed of a population of N creatures, each with a
 * different, but sexually compatible, genotype.
 *
 * 1) Creatures die off.
 * 2) Creatures regenerate.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @author <a href="damiansp@stanford.edu">Damian Satterthwaite-Phillips</a>
 * @version $Revision 1.0$
 */
class Test {

    /** Population size. */
    final int psize;

    /** Number of generations. */
    final int numgen;

    /** Altruism rate. */
    final float altrate;

    /** Predation rate. */
    final float predrate;

    /** Random variable. */
    final Random random;

    final List<Creature> creatures;

    /** The number of altruistic creatures in the current
     * generation. */
    int mAltCount;

    /**
     * Construct list of creatures with given test parameters.
     */
    Test(int psize, int numgen, float altrate, float predrate, long seed) {
        this.psize = psize;
        this.numgen = numgen;
        this.altrate = altrate;
        this.predrate = predrate;
        random = new Random(seed);

        final int numAlt = (int) ((float) psize * altrate);
        creatures = new LinkedList<Creature>();
        for (int i = 0; i < psize; i++) {
            final Gene [] g1 = new Gene[]{new Gene("altruism", i < numAlt, i < (numAlt * 2))};
            final Gene [] g2 = new Gene[]{new Gene("altruism", i < numAlt, i < (numAlt * 2))};
            creatures.add(new Creature(g1, g2));
        }
    }

    /**
     * Randomly remove predrate * psize number of creatures from
     * population.
     */
    void predate() {

        final int numPred = (int) ((float) psize * predrate);

        Collections.shuffle(creatures, random);

        for (int i = 0; i < numPred; i++)
            creatures.remove(0);
    }

    /**
     * Grow population back to psize by mating each pair of neighbors.
     */
    void procreate() {

        final List<Creature> babies = new LinkedList<Creature>();

        for (int i = 0, numToGenerate = psize - creatures.size(); i < numToGenerate; i++) {

            final Creature c1 = creatures.get(i);
            if (i + 1 < creatures.size()) {
                final Creature c2 = creatures.get(i + 1);
                babies.add(c1.mate(c2));
            }
        }

        creatures.addAll(babies);
    }

    /**
     * Evolve population for the specified number of generations.
     */
    void generate() {

        System.out.print(VT100.CLEAR_SCREEN);
        System.out.print(VT100.CURSOR_HOME);

        for (int i = 0; i < numgen; i++) {
            predate();
            procreate();
            mAltCount = 0;
            for (int j = 0, n = creatures.size(); j < n; j++) {
                if (creatures.get(j).genes[0].isExpressed()) {
                    System.out.print(VT100.cursorForce(1, j) + "A");
                    mAltCount++;
                } else {
                    System.out.print(VT100.cursorForce(1, j) + "S");
                }
                
                // if (Boolean.getBoolean("debug"))
                //      System.err.println(c);
            }

            if (mAltCount == 0 || mAltCount == psize)
                break;
        }

        System.out.println();
    }

    /**
     * Make a descriptive string of the parameters for the test.
     */
    public String toString() {

        final StringBuffer buf = new StringBuffer();
        buf.append("Population Size: ").append(creatures.size());
        buf.append("\nNum Altruistic:  ").append(mAltCount);
        return buf.toString();
    }

    /**
     * Command-line interface.  To perform a test, run "java Test".
     */
    public static void main(String [] args) {
        final int psize = Integer.getInteger("psize", 100);
        final int numgen = Integer.getInteger("numgen", 100);
        final float altrate = Float.parseFloat(System.getProperty("altrate", "0.1"));
        final float predrate = Float.parseFloat(System.getProperty("predrate", "0.1"));
        final long seed = Long.parseLong(System.getProperty("seed", Long.toString(System.currentTimeMillis())));

        final StringBuffer buf = new StringBuffer();
        buf.append("psize (Population Size):        ").append(psize);
        buf.append("\nnumgen (Number of Generations): ").append(numgen);
        buf.append("\naltrate (Altruism Rate):        ").append(altrate);
        buf.append("\npredrate (Predation Rate):      ").append(predrate);
        buf.append("\nseed (Random Seed):             ").append(seed);
        //        System.out.println(buf.toString());

        final Test t = new Test(psize, numgen, altrate, predrate, seed);
        t.generate();
    }
}
