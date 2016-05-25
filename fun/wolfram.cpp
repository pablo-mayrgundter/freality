#include <iostream>
#include <sys/ioctl.h>

// http://mathworld.wolfram.com/CellularAutomaton.html
void draw(int rows, int cols, int rule) {
  std::string color[] = {"\033[1;40m", "\033[1;41m"};
  int input[cols], output[cols], arrSize = sizeof(int) * cols;
  bzero(input, arrSize);
  bzero(output, arrSize);
  input[cols / 2] = 1;
  std::string os = "";
  for (int r = 0; r < rows - 2; r++) {
    for (int c = 0; c < cols; c++) {
      os += color[input[c]] + " ";
      int l = 0, m = input[c], r = 0;
      if (c == 0) {
        l = input[cols - 1]; // wrap to right side.
        r = input[c + 1];
      } else if (c == cols - 1 ) {
        r = input[0]; // wrap to left side.
        l = input[c - 1];
      } else {
        l = input[c - 1];
        r = input[c + 1];
      }
      int in = l << 2 | m << 1 | r;
      output[c] = (rule >> in) & 1;
    }
    bcopy(output, input, arrSize);
    os += color[0] + "\n";
  }
  printf("\033[2J\033[0;0frule: %d\n%s", rule, os.c_str());
}

int main(int argc, char** argv) {
  int rule = atoi(argc <= 1 ? "30" : argv[1]);
  struct winsize ws;
  ioctl(0, TIOCGWINSZ, &ws);
  draw(ws.ws_row, ws.ws_col, rule);
  return 0;
}
