package game.cae;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Cards Against Entropy.
 */
class Game {

  Set<Player> players;

  Game(int numPlayers) {
    players = new LinkedHashSet<Player>();
    players.add(new Player(players.size() + ""));
    players.add(new Player(players.size() + ""));
  }

  void start() {
    System.out.println("Starting!");
  }

  boolean turn() {
    System.out.println("Players receive sunlight!");
    for (Player p : players) {
      p.receiveNewEnergy(p.getPlantPopulation());
    }
    System.out.println("Player turns...");
    for (Player p : players) {
      p.go();
    }
    return true;
  }

  public static void main(String [] args) {
    Game g = new Game(2);
    g.start();
    while (g.turn());
  }
}
