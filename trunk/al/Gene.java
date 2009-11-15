package al;

/**
 * A gene, represented as a two-part allele.
 */
class Gene {

    final String name;
    // An allele.
    final boolean allele1;
    final boolean allele2;

    Gene(String name, boolean allele1, boolean allele2) {
        this.name = name;
        this.allele1 = allele1;
        this.allele2 = allele2;
    }

    boolean isExpressed() {
        return allele1 && allele2;
    }

    public String toString() {
        final char c = name.charAt(0);
        final StringBuffer buf = new StringBuffer("gene: ");
        buf.append(allele1 ? Character.toUpperCase(c) : Character.toLowerCase(c));
        buf.append(allele2 ? Character.toUpperCase(c) : Character.toLowerCase(c));
        buf.append(" (").append(name).append(")");
        return buf.toString();
    }
}
