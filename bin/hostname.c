/**
 * UNIX-style hostname command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define HOST_NAME_MAX 256

int main (const int argc, const char * const argv[]) {
  if (argc == 1) {
    char name[HOST_NAME_MAX];
    if (gethostname(name, HOST_NAME_MAX) == 0) {
      printf("%s\n", name);
    } else {
      printf("Error: %s\n", strerror(errno));
      return 1;
    }
  } else if (argc == 2) {
    // checks...
    const char * name = argv[1];
    int len = strlen(argv[1]);
    if (sethostname(name, len)) {
      printf("Error: %s\n", strerror(errno));
      return 1;
    }
  }
  return 0;
}
