/**
 * UNIX-style echo command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <stdio.h>
#include <string.h>

int main (const int argc, const char * const argv[]) {
  const int NO_RET = argc > 1 && strncmp(argv[1], "-n", 3) == 0 ? 1 : 0;
  int i;
  for (i = NO_RET ? 2 : 1; i < argc; i++)
    printf("%s%s", argv[i], i == argc - 1 ? "" : " ");
  if (!NO_RET)
    printf("\n");
  return 0;
}
