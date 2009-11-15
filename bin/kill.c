/**
 * UNIX-style kill command.
 *
 * @author Pablo Mayrgundter
 * @date Sunday, June 15, 2008
 */
#include <sys/types.h>
#include <signal.h>
#include <stdlib.h>

int main (const int argc, const char * const argv[]) {
  const int pid = atoi(argv[1]);
  const int sig = atoi(argv[2]);
  kill(pid, sig);
}
