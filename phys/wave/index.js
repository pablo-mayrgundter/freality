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
  const phaseDelta = (i++ / 30) * Tau;
  clear(c);
  //axes(c);
  //sinX(c, d);
  //sinY(c, d);
  //pixelWaves(c, d);
  //mergeSplit(c, d);
  randomWaves(c, phaseDelta);
  window.requestAnimationFrame(anim);
}

function r1(max) {
  max = max || Tau;
  return 2 * max * Math.random() - max;
}

const c1 = r1(), c2 = r1(), c3 = r1(), c4 = r1(), c5 = r1(), c6 = r1(), c7 = r1(), c8 = r1();
const f1 = r1(), f2 = r1(f1), f3 = r1(f2), f4 = r1(f3), f5 = r1(f4), f6 = r1(f5);
const
  i1 = r1(64), i2 = r1(64), i3 = r1(64),
  i4 = r1(64), i5 = r1(64), i6 = r1(64),
  i7 = r1(64), i8 = r1(64), i9 = r1(64),
  i10 = r1(64);

function randomWaves(c, pD) {
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x = -Pi + i / N * Tau;
      const y = -Pi + j / N * Tau;

      const x1 = x + c1 * Math.sin(f2 * x + pD);
      const y1 = y + c2 * Math.cos(f2 * y + pD);

      const x2 = x1 + c3 * Math.sin(f3 * x1);
      const y2 = y1 + c4 * Math.cos(f3 * y1);

      const x3 = x2 + c5 * Math.sin(f4 * x2);
      const y3 = y2 + c6 * Math.cos(f4 * y2);

      const x4 = x3 + c7 * Math.sin(f5 * x3);
      const y4 = y3 + c8 * Math.cos(f5 * y3);

      const a1 = x1 + y1;
      const a2 = x2 + y2;
      const a3 = x3 + y3;
      const a4 = x4 + y4;

      const r = i3 * a4 + i2 * a3 + i1 * a2;
      const g = i2 * a4 + i1 * a3 + i3 * a2;
      const b = i1 * a4 + i3 * a3 + i2 * a2;
      c.fillStyle = `rgb(${r}, ${g}, ${b})`;
      c.fillRect(xToC(x), yToC(y), 0.03, 0.03);
    }
  }
}

function mergeSplit(c, pD) {
  const f = 1;
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = -Pi + i / N * Tau;
      const x2 = -Pi + j / N * Tau;
      const y1 = Math.sin(f * x1 + pD) + Math.cos(f * x2 + pD);
      const y2 = Math.sin(f * -x1 + pD) + Math.cos(f * -x2 + pD);
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