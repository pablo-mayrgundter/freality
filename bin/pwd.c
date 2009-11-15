/**
 * UNIX-style pwd command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <unistd.h>
#include <stdio.h>

int main (const int argc, const char * const argv[]) {
  const char * buf = getcwd(NULL, 0);
  if (buf == NULL)
    return 1;
  printf("%s\n", buf);
  return 0;
}
