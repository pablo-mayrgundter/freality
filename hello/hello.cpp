#include <stdlib.h>
#include <iostream.h>

int main (int, char **);
int add (int, int);

int main (int argc, char ** argv) {
  int x = 3;
  int y = 4;
  int sum = add(x, y);
  cout << "sum: " << sum << endl;
}

int add(int x, int y){
  return x + y;
}
