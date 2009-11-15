function Term(screenElt) {

  var SPACE = ' ';

  this.display = screenElt;
  var w = 10, h = 10;
  this.display.innerHTML = '<span>asdf</span>';
  // Probe size for large display.
  if (this.display.offsetWidth > 100 && this.display.offsetHeight > 100) {
    w = h = 0;
    while (this.display.offsetWidth > this.display.firstChild.offsetWidth) {
      this.display.firstChild.innerHTML += SPACE;
      w++;
      if (w > 1000)
        break;
    }
    if (w>0)
      w--;
    while (this.display.offsetHeight > this.display.firstChild.offsetHeight) {
      this.display.firstChild.innerHTML += SPACE+'<br/>';
      h++;
      if (h > 1000)
        break;
    }
    if (h>0)
      h--;
  }
  this.display.innerHTML = '';
  this.width = w;
  this.height = h;
  this.buf = new Array(this.height);
  this.row = 0;
  this.col = 0;

  Term.prototype.init = function() {
    for (var row = 0; row < this.height; row++) {
      this.buf[row] = new Array(this.width);
      for (var col = 0; col < this.width; col++) {
        this.buf[row][col] = SPACE;
      }
    }
  };
  this.init();

  Term.prototype.clear = function() {
    for (var row = 0; row < this.height; row++)
      for (var col = 0; col < this.width; col++)
        this.buf[row][col] = SPACE;
    this.redraw();
  };

  Term.prototype.cursorForce = function(row, col) {
    this.row = row;
    this.col = col;
  };

  Term.prototype.handleKey = function(code) {
    switch(code) {
    case 8: { // Delete
      if (this.col == 0) {
        if (this.row == 0)
          return;
        if (this.row > 0) {
          this.row--;
          this.col = this.width;
        }
      }
      this.buf[this.row][--this.col] = SPACE;
      this.redraw();
    }; break;
    case 13: { // Return
      this.ret();
      this.redraw();
    }; break;
    case 16: break; // Shift, so not skipped in default.
    case 37: this.col--; this.redraw(); break;
    case 38: this.row--; this.redraw(); break;
    case 39: this.col++; this.redraw(); break;
    case 40: this.row++; this.redraw(); break;
    default: {
      this.print(String.fromCharCode(code));
    }
    };
  };
  var me = this;
  document.onkeypress = function(e) {
    if (!e)
      e = window.event;
    code = e.keyCode;
    if (!code)
      code = e.which;
    me.handleKey(code);
    return false;
  };

  Term.prototype.ret = function() {
    if (this.row == this.height - 1)
      return;
    this.col = 0;
    this.row++;
  };

  Term.prototype.print = function(s) {
    var rowStart = this.row;
    var colStart = this.col;
    for (var ndx = 0; ndx < s.length; ndx++) {
      var c = s.charAt(ndx);
      if (c == '\n') {
        this.ret();
        continue;
      }
      this.buf[this.row][this.col++] = c;
      if (this.col == this.width)
        this.ret();
    }
    this.redraw();
  };

  Term.prototype.println = function(s) {
    this.print(s+'\n');
    this.redraw();
  };

  Term.prototype.redraw = function() {
    var s = '';
    for (var row = 0; row < this.height; row++) {
      for (var col = 0; col < this.width; col++) {
        if (row == this.row && col == this.col)
          s += '<span style="background-color: white">'+this.buf[row][col]+'</span>';
        else
          s += this.buf[row][col];
      }
      s += '<br/>';
    }
    this.display.innerHTML = s;
  };
}
