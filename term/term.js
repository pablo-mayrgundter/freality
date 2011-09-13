/**
 * Create a new terminal in the given dom element.
 */
function Term(screenEltId) {

  var SPACE = '_';

  this.display = document.getElementById(screenEltId);
  this.cols = this.display.cols;
  this.rows = this.display.rows;
  this.buf = new Array(this.rows);
  this.row = 0;
  this.col = 0;

  Term.prototype.init = function() {
    for (var row = 0; row < this.rows; row++) {
      this.buf[row] = new Array(this.cols);
      for (var col = 0; col < this.cols; col++) {
        this.buf[row][col] = SPACE;
      }
    }
  };
  this.init();

  Term.prototype.clear = function() {
    for (var row = 0; row < this.rows; row++)
      for (var col = 0; col < this.cols; col++)
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
          this.col = this.cols;
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
    default: {
      this.print(String.fromCharCode(code));
    }
    };
  };

  this.shift = false;

  var me = this;

  document.onkeypress = function(e) {
    if (!e) {
      e = window.event;
    }
    code = e.keyCode;
    if (!code) {
      code = e.which;
    }
    me.handleKey(code);
    return false;
  };

  document.onkeydown = function(e) {
    switch (e.keyCode) {
    case 8: me.handleKey(e.keyCode); return false; // backspace.
    case 37: me.col--; break; // left
    case 38: me.row--; break; // up
    case 39: me.col++; break; // right
    case 40: me.row++; break; // down
    }
    me.updateCursor();
  };

  document.onkeyup = function(e) {
    if (e.shiftKey) {
      me.shift = false
    }
  };

  Term.prototype.ret = function() {
    if (this.row == this.rows - 1) {
      return;
    }
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
      if (this.col == this.cols) {
        this.ret();
      }
    }
    this.redraw();
  };

  Term.prototype.println = function(s) {
    this.print(s+'\n');
    this.redraw();
  };

  Term.prototype.redraw = function() {
    var s = '';
    for (var row = 0; row < this.rows; row++) {
      for (var col = 0; col < this.cols; col++) {
        s += this.buf[row][col];
      }
      s += '\n';
    }
    this.display.innerHTML = s;
    me.updateCursor();
  };

  Term.prototype.updateCursor = function() {
    var linearOffset = (this.cols + 1) * this.row + this.col;
    this.display.setSelectionRange(linearOffset, linearOffset);
  };
}
