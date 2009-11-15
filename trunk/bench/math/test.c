#include "math.h"
int main(int argc, char ** argv) {
  const int iterations = atoi(argv[1]);
  int i;
  for (i = 0; i < iterations; i++)
    sin(i);
}
