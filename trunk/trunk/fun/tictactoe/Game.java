package fun.tictactoe;

class Game {

  final int [][] mSquares = new int[3][3];
  int mNumMoves = 0;
  int mWinner = -1;

  boolean isFinished() {
    // Check rows.
    int count = 0;
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++)
        count += mSquares[row][col];
      if (threeCountThenSetWinner(count))
        return true;
      count = 0;
    }
    // Check cols.
    for (int col = 0; col < 3; col++) {
      for (int row = 0; row < 3; row++)
        count += mSquares[row][col];
      if (threeCountThenSetWinner(count))
        return true;
      count = 0;
    }
    // Check downslash diag.
    count += mSquares[0][0];
    count += mSquares[1][1];
    count += mSquares[2][2];
    if (threeCountThenSetWinner(count))
      return true;
    count = 0;

    // Check upslash diag.
    count += mSquares[2][0];
    count += mSquares[1][1];
    count += mSquares[0][2];
    if (threeCountThenSetWinner(count))
      return true;

    if (mNumMoves == 9)
      return true;

    return false;
  }

  boolean threeCountThenSetWinner(final int count) {
    if (count == 3)
      mWinner = 1;
    else if (count == -3)
      mWinner = 0;
    return mWinner != -1;
  }

  int getSquare(final int row, final int col) {
    return mSquares[row][col];
  }

  int getWinner() {
    return mWinner;
  }

  boolean move(final boolean player, final int move) {
    if (move < 0 || move > 9)
      return false;
    int row = move / 3;
    int col = move % 3;
    if (mSquares[row][col] != 0)
      return false;
    mSquares[row][col] = player ? 1 : -1;
    mNumMoves++;
    return true;
  }
}
