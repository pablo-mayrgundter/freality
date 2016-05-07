class Color {
  int r, g, b;
  Color(this.r, this.g, this.b);
  String toCssString() {
    var rs = r.toRadixString(16);
    if (r < 16)
      rs = '0' + rs;
    var gs = g.toRadixString(16);
    if (g < 16)
      gs = '0' + gs;
    var bs = b.toRadixString(16);
    if (b < 16)
      bs = '0' + bs;
    return '#' + rs + gs + bs;
  }
}
