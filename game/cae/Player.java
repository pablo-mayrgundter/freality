package game.cae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A player.  Nay, A God!
 */
class Player {

  static final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

  final String name;
  int freeEnergy;
  int plants;
  Player(String name) {
    this.name = name;
    plants = 1;
  }

  public String toString() {
    return String.format("Player{name: %s, energy: %d}", name, freeEnergy);
  }

  void receiveNewEnergy(int newEnergy) {
    freeEnergy = newEnergy;
  }

  /** Used to allocate new energy. */
  int getPlantPopulation() {
    return plants;
  }

  enum Move {
    HOLD, NEW, MUTATE;
  }

  /** Interactive player move. */
  void go() {
    System.out.println("player: " + this);
    System.out.println("move? (hold/new/mutate)");
      outer:
    do {
      Move m = Move.valueOf(getCmd().toUpperCase());
      switch (m) {
      case HOLD: break outer;
      case NEW: newCreature(); break outer;
      case MUTATE: mutate(); break outer;
      default: break;
      }
    } while (true);
  }

  String getCmd() {
    try {
      return r.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  void newCreature() {
    System.out.println("type? (plant/animal)");
    String type = getCmd();
  }

  void mutate() {
    System.out.println("mutation? (foo/bar)");
    String mut = getCmd();
  }

  int returnExcessEnergy() {
    return freeEnergy;
  }
}
