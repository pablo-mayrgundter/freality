let c;

function init() {
  const canvas = document.getElementById('canvas');
  const size = Math.min(window.innerWidth, window.innerHeight);
  canvas.width = size;
  canvas.height = size;
  c = canvas.getContext('2d');
  c.scale(canvas.width / 2, canvas.height / 2);
  window.requestAnimationFrame(anim);
}

let i = 0;

function anim(timestamp) {
  const d = Math.PI * 2 * (i++ / 100);
  clear(c);
  //axes(c);
  //sinX(c, d);
  //sinY(c, d);
  //pixelWaves(c, d);
  //mergeSplit(c, d);
  rotateBlobs(c, d);
  window.requestAnimationFrame(anim);
}

function clear(c) {
  c.fillStyle = '#fff';
  c.fillRect(0, 0, xToC(4), yToC(4));
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

function rotateBlobs(c, phase) {
  const N = 100, Tau = 2 * Math.PI;
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = -Math.PI + i / N * Tau;
      const x2 = -Math.PI + j / N * Tau;
      const y1 = Math.sin(x1 + phase) + Math.cos(x1 - phase);
      const y2 = Math.sin(x2 - phase) + Math.cos(x2 + phase);
      const intensity = 128 * y1 + 128 * y2;
      c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
      c.fillRect(xToC(x1), yToC(x2), 0.03, 0.03);
    }
  }
}

function mergeSplit(c, phase) {
  const N = 100, Tau = 2 * Math.PI;
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = -Math.PI + i / N * Tau;
      const x2 = -Math.PI + j / N * Tau;
      const y1 = Math.sin(x1 + phase);
      const y2 = Math.cos(x2 + phase);
      const y3 = Math.sin(-x1 + phase);
      const y4 = Math.cos(-x2 + phase);
      const intensity = 64 * y1 + 64 * y2 + 64 * y3 + 64 * y4;
      c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
      c.fillRect(xToC(x1), yToC(x2), 0.03, 0.03);
    }
  }
}

function pixelWaves(c, phase) {
  const N = 100, Tau = 2 * Math.PI;
  c.fillStyle = '#000000';
  for (let i = 0; i < N; i++) {
    const dX = i / N * Tau;
    const x = -Math.PI + dX;
    const y = Math.sin(dX + phase);
    c.fillRect(xToC(x), yToC(y), 0.03, 0.03);
    c.fillRect(xToC(y), yToC(x), 0.03, 0.03);
  }
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
  return 0.25 * x + 1;
}

function yToC(y) {
  return 0.25 * y + 1;
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