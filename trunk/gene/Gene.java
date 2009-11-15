package gene;

public abstract class Gene {
  int fitness;
  public Gene() {}
  public void bless() { fitness=1; }
  public void curse() { fitness=-1; }
  abstract Gene cross(Gene other);
  abstract Gene mutate();
}