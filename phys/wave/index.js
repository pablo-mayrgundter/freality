let c;

function init() {
  const canvas = document.getElementById('canvas');
  const size = Math.min(window.innerWidth, window.innerHeight);
  canvas.width = size;
  canvas.height = size;
  c = canvas.getContext('2d');
  c.scale(canvas.width, canvas.height);
  window.requestAnimationFrame(anim);
}

let i = 0;

function anim(timestamp) {
  const d = Math.PI * 2 * (i++ / 100);
  clear(c);
  axes(c);
  sinX(c, d);
  sinY(c, d);
  window.requestAnimationFrame(anim);
}

function clear(c) {
  c.fillStyle = '#fff';
  c.fillRect(0, 0, xToC(1), yToC(1));
}

function line(c, x1, y1, x2, y2) {
  begin(c);
  c.moveTo(xToC(x1), yToC(y1));
  c.lineTo(xToC(x2), yToC(y2));
  end(c);
}

function axes(c) {
  // X-axis
  line(c,  -1,   0,   1,   0);
  // Y-axis
  line(c,   0,  -1,   0,   1);
}

function sinX(c, phase) {
  begin(c);
  for (let i = 0; i < 100; i++) {
    const d = i / 100 * (Math.PI * 2);
    const x = -Math.PI + d;
    const y = Math.sin(x + phase);
    c.lineTo(xToC(x), yToC(y));
  }
  end(c);
}

function sinY(c, phase) {
  begin(c);
  for (let i = 0; i < 100; i++) {
    const d = i / 100 * (Math.PI * 2);
    const x = -Math.PI + d;
    const y = Math.sin(x + phase);
    c.lineTo(xToC(y), yToC(x));
  }
  end(c);
}

function xToC(x) {
  return 0.5 * x + 0.5;
}

function yToC(y) {
  return 0.5 * y + 0.5;
}

function begin(c) {
  c.beginPath();
  c.lineWidth = '0.01';
  c.strokeStyle = 'black';
}

function end(c) {
  c.stroke();
}

init();