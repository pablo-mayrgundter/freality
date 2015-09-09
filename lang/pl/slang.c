#include <stdio.h>

typedef struct class {
  int data;
  void (*func)(struct class);
  struct class* this;
} Class;

void printInt(Class c) {
  printf("%d\n", c.data);
}

int main(int argc, char** argv) {
  Class d;
  d.data = 1;
  d.func = printInt;
  d.this = &d;
  d.func(d);
}
