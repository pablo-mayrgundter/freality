#import <bits/time.h>
int main(int argc, char ** argv) {
  int MHZ = 1395.807 * 1000000.0;
  const int numIts = (double)MHZ / 1.525;
  printf("#cycles per sec, clocks per sec, its in for loop\n");
  printf("%d, %d, %d\n", MHZ, CLOCKS_PER_SEC, numIts);
  int i,t,diff;
  while (1) {
    t = clock();
    for (i = 0; i < numIts; i++)
      ;
    t = clock() - t;
    diff = CLOCKS_PER_SEC - t;
    printf("%d (%f%%)\n", diff, ((float)diff/(float)CLOCKS_PER_SEC));
  }
}
