/**
 * UNIX-style sleep command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <unistd.h>
#include <stdlib.h>
#include <string.h>

int main (const int argc, const char * const argv[]) {
  if (argc != 2)
    return 1;
  const int secs = atoi(argv[1]);
  if (secs == 0 && strncmp(argv[1], "0", 1) != 0)
    return 1;
  sleep(secs);
  return 0;
}
