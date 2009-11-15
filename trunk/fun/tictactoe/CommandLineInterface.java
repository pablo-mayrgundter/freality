package fun.tictactoe;

import java.io.*;

class CommandLineInterface {

  final BufferedReader mIn;
  final Game mGame;
  final String [] mPlayers;

  CommandLineInterface() throws IOException {
    mIn = new BufferedReader(new InputStreamReader(System.in));
    mGame = new Game();
    mPlayers = new String[2];
  }

  public static void main (String [] args) throws IOException {
    CommandLineInterface cli = new CommandLineInterface();
    cli.runGame();
  }

  void runGame() {
    promptPlayerNames();
    promptMoves();
  }

  void promptPlayerNames() {
    mPlayers[0] = promptPlayerName("First player's name: ");
    mPlayers[1] = promptPlayerName("Second player's name: ");
  }

  void promptMoves() {
    int turn = 0;
    while (!mGame.isFinished()) {
      printBoard();
      final int playerId = turn % 2;
      final String moveStr = prompt(mPlayers[playerId] +", your move ("+ (playerId == 0 ? "X" : "O") +"):");
      int move;
      try {
        move = Integer.parseInt(moveStr) - 1;
      } catch (NumberFormatException e) {
        System.err.println("Move must be a number.");
        continue;
      }
      if (move < 0 || move > 8) {
        System.err.println("Move must be a number, 1-9.");
        continue;
      }
      if (!mGame.move(playerId == 1, move)) {
        System.err.println("Illegal move!");
        continue;
      }
      turn++;
    }
    printBoard();
    final int winner = mGame.getWinner();
    if (winner == -1)
      System.out.println("Cats game!");
    else 
      System.out.println("Winner is: "+ mPlayers[winner]);
  }

  String promptPlayerName(final String msg) {
    while (true) {
      final String name = prompt(msg);
      if (name.matches("\\w+(?:\\s+\\w+)*"))
        return name;
      System.err.println("Name must match: \\w+(\\s+\\w+)*");
    }
  }

  String prompt(final String msg) {
    try {
      System.out.println(msg);
      return mIn.readLine().trim();
    } catch (IOException e) {
      throw new RuntimeException("Sorry, there was a problem reading your input :(");
    }
  }

  void printBoard() {
    String s = "-------\n";
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        final int var = mGame.getSquare(row, col);
        if (var == 0)
          s += "|"+ (row * 3 + col + 1);
        else
          s += "|"+ (var == 1 ? "O" : "X");
      }
      s += "|\n";
    }
    s += "-------";
    System.out.println(s);
  }
}
