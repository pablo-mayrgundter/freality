package gene;

import java.util.Random;

public class StringGene extends Gene {

  static final Random R = new Random();

  final String genome;

  public StringGene(final String genome) {
    this.genome = genome;
  }

  public Gene cross(final Gene otherGene) {
    StringGene other = (StringGene)otherGene;
    String newGenome = this.genome.substring(R.nextInt(this.genome.length()))
      + other.genome.substring(R.nextInt(other.genome.length()));
    return new StringGene(newGenome);
  }

  public Gene mutate() {
    final StringBuffer buf = new StringBuffer(genome);
    Util.flipChar(buf);
    return new StringGene(buf.toString());
  }

  public String toString() {
    return genome+"("+fitness+")";
  }
}