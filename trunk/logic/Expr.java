package logic;

class Expr {

  final String mSymbol;
    
  Expr (final String s) {
    mSymbol = s;
  }

  public String toString () {
    return mSymbol;
  }
}
