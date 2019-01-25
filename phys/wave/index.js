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
const N = 100, Pi = Math.PI, Tau = 2 * Pi;

function anim(timestamp) {
  const d = Math.PI * 2 * (i++ / 100);
  clear(c);
  //axes(c);
  //sinX(c, d);
  //sinY(c, d);
  //pixelWaves(c, d);
  //mergeSplit(c, d);
  randomWaves(c, d);
  window.requestAnimationFrame(anim);
}

function r1() {
  return 2 * Tau * Math.random() - Tau;
}

const c1 = r1(), c2 = r1(), c3 = r1(), c4 = r1(), c5 = r1(), c6 = r1();
const f1 = r1(), f2 = r1(), f3 = r1(), f4 = r1(), f5 = r1(), f6 = r1();
function randomWaves(c, phase) {
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = -Pi + i / N * Tau;
      const x2 = -Pi + j / N * Tau;
      const y1 = c1 * Math.sin(f1 * x1 + phase) + c2 * Math.cos(f2 * x2 + phase);
      const y2 = c3 * Math.sin(f3 * y1 + phase) + c4 * Math.cos(f4 * y1 + phase);
      const y3 = c5 * Math.sin(f5 * y2 + phase) + c6 * Math.cos(f6 * y2 + phase);
      const intensity = 85.3 * y1 + 85.3 * y2 + 85.3 * y3;
      c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
      c.fillRect(xToC(x1), yToC(x2), 0.03, 0.03);
    }
  }
}

function mergeSplit(c, phase) {
  const f = 1;
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = -Pi + i / N * Tau;
      const x2 = -Pi + j / N * Tau;
      const y1 = Math.sin(f * x1 + phase) + Math.cos(f * x2 + phase);
      const y2 = Math.sin(f * -x1 + phase) + Math.cos(f * -x2 + phase);
      const intensity = 128 * y1 + 128 * y2;
      c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
      c.fillRect(xToC(x1), yToC(x2), 0.03, 0.03);
    }
  }
}

function pixelWaves(c, phase) {
  c.fillStyle = '#000000';
  for (let i = 0; i < N; i++) {
    const dX = i / N * Tau;
    const x = -Pi + dX;
    const y = Math.sin(dX + phase);
    c.fillRect(xToC(x), yToC(y), 0.03, 0.03);
    c.fillRect(xToC(y), yToC(x), 0.03, 0.03);
  }
}

function sinX(c, phase) {
  begin(c);
  for (let i = 0; i < 100; i++) {
    const d = i / 100 * (Pi * 2);
    const x = -Pi + d;
    const y = Math.sin(x + phase);
    c.lineTo(xToC(x), yToC(y));
  }
  end(c);
}

function sinY(c, phase) {
  begin(c);
  for (let i = 0; i < 100; i++) {
    const d = i / 100 * (Pi * 2);
    const x = -Pi + d;
    const y = Math.sin(x + phase);
    c.lineTo(xToC(y), yToC(x));
  }
  end(c);
}

function axes(c) {
  // X-axis
  line(c,  -1,   0,   1,   0);
  // Y-axis
  line(c,   0,  -1,   0,   1);
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