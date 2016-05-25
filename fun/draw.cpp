#include <iostream>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <termios.h>

using std::cin;
using std::cout;
using std::cerr;
using std::endl;

int cols, rows;         // see setupTerm
int row = 0, col = 0;   /* turtle location */
int heading = 0;        /* turtle's heading 0: E, 1: S, 2: W, 3: N */
struct termios oldtermios;
const char* icon = ">";
int color = 40;

int setupTerm() {
  // Get terminal size.
  // http://stackoverflow.com/questions/1022957/getting-terminal-width-in-c
  struct winsize ws;
  ioctl(0, TIOCGWINSZ, &ws);
  rows = ws.ws_row;
  cols = ws.ws_col;

  // Turn terminal to raw mode.
  // http://www.minek.com/files/unix_examples/raw.html
  if (tcgetattr(0, &oldtermios) < 0) {
    return -1;
  }
  struct termios newtermios;
  newtermios = oldtermios;
  newtermios.c_lflag &= ~(ICANON);
  newtermios.c_cc[VMIN] = 1;
  newtermios.c_cc[VTIME] = 0;
  if (tcsetattr(0, TCSAFLUSH, &newtermios) < 0) {
    return -1;
  }
  return 1;
}

int ttyreset() {
  if (tcsetattr(0, TCSAFLUSH, &oldtermios) < 0) {
    return -1;
  }
  return 1;
}

// Helpers for VT100 terminal codes.
// http://www.ccs.neu.edu/research/gpc/MSim/vona/terminal/VT100_Escape_Codes.html
void clear() {
  printf("\033[2J");
}

void draw(int row, int col, const char* str) {
  printf("\033[%d;%df%s", row, col, str);
}

void draw(const char* str) {
  draw(row, col, str);
}

void setColor(int c) {
  color = c;
}

void turn(int direction) {
  if (direction == 0) { /* turn left */
    heading = --heading < 0 ? 3 : heading;
  } else if (direction == 1) { /* turn right */
    heading++;
    heading %= 4;
  } else {
    printf("Bad turn direction input %d", direction);
  }
  switch (heading) {
  case 0: icon = ">"; break; /* E */
  case 1: icon = "v"; break; /* S */
  case 2: icon = "<"; break; /* W */
  case 3: icon = "^"; break; /* N */
  default: printf("Bad heading: %d\n", heading);
  }
  draw(icon);
}

void move() {
  printf("\033[1;%dm", color);
  draw(" ");
  switch (heading) {
  case 0: col = std::min(col + 1, cols);     break; /* E */
  case 1: row = std::min(row + 1, rows - 1); break; /* S */
  case 2: col = std::max(col - 1, 0);        break; /* W */
  case 3: row = std::max(row - 1, 0);        break; /* N */
  default:
    printf("Bad heading value %d", heading);
    break;
  }
  draw(icon);
}

int readCmd() {
  draw(rows, 0,
"\033[1;40mw: forward, a: left, d: right: q: quit; colors:\
\033[1;40m0,\
\033[1;41m1,\
\033[1;42m2,\
\033[1;43m3,\
\033[1;44m4,\
\033[1;45m5,\
\033[1;46m6,\
\033[1;47m7,\
\033[1;48m8,\
\033[1;49m9,\
\033[1;40m - command? ");
  // switch on raw ascii codes.
  int c = getchar();
  if (c >= 48 && c <= 57) {
    setColor(c - 8);
  } else {
    switch (c) {
    case 97: turn(0);  break; // a
    case 100: turn(1); break; // d
    case 119: move();  break; // w
    case 113: return   -1;    // q
    }
  }
  return 0;
}

int main() {
  if (!setupTerm()) {
    cerr << "term setup error" << endl;
    return -1;
  }
   clear();
  draw(icon);
  while (readCmd() != -1);
  draw(rows, 0, "\033[K"); // clear prompt without scrolling drawing
  return ttyreset();
}
