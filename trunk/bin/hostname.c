/**
 * UNIX-style hostname command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <errno.h>
#include <stdio.h>
#include <unistd.h>

#define HOST_NAME_MAX 256

int main (const int argc, const char * const argv[]) {
  if (argc == 1) {
    char name[HOST_NAME_MAX];
    if (gethostname(name, HOST_NAME_MAX) == 0) {
      printf("%s\n", name);
    } else {
      if (errno == ENAMETOOLONG) {
        printf("Name too long\n");
      }
      return -1;
    }
  }
  return 0;
}
