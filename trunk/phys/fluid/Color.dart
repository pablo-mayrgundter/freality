part of phys;

class Color {
  int r, g, b;
  Color(this.r, this.g, this.b);
  String toCssString() {
    var rs = r == 0 ? '00' : r.toRadixString(16);
    var gs = g == 0 ? '00' : g.toRadixString(16);
    var bs = b == 0 ? '00' : b.toRadixString(16);
    return '#' + rs + gs + bs;
  }
}
