package al;

/**
 * A simple creature consisting of only one gene for altruism.
 *
 * @author <a href="pablo@freality.com">Pablo Mayrgundter</a>
 * @author <a href="damiansp@stanford.edu">Damian Satterthwaite-Phillips</a>
 * @version $Revision 1.0$
 */
class Creature {

    static final boolean ALTRUISM_DOMINANT = Boolean.getBoolean("altruism_dominant");
    static {
        System.out.println("Altruism Dominant: " + ALTRUISM_DOMINANT);
    }
    static long COUNT = 0;

    final Gene [] genes;
    final long id;

    Creature(final Gene [] motherGenes, final Gene [] fatherGenes) {

        if (motherGenes.length != fatherGenes.length)
            throw new IllegalArgumentException("Mother and father genes don't pair.");

        genes = new Gene[motherGenes.length];

        for (int i = 0; i < genes.length; i++)
            genes[i] = new Gene(motherGenes[i].name, motherGenes[i].allele1, fatherGenes[i].allele2);

        id = COUNT++;
    }

    Creature mate(final Creature partner) {
        return new Creature(genes, partner.genes);
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer("creature: ");
        buf.append(id).append('\n');
        for(Gene g : genes)
            buf.append(g);
        return buf.toString();
    }
}
