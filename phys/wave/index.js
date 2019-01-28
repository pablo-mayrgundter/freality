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
const N = 128, Pi = Math.PI, Tau = 2 * Pi;
const fromX = -2 * Tau;
const width = 4 * Tau;
const pixelSize = 0.02;

function anim(timestamp) {
  const phaseDelta = i++ / (N / 8);
  clear(c);
  //axes(c);
  //sinX(c, phaseDelta);
  //sinX(c, phaseDelta, Pi);
  //sinY(c, phaseDelta);
  //sinY(c, phaseDelta, Pi);
  //pixelWaves(c, phaseDelta);
  //mergeSplit(c, phaseDelta);
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
      const x = fromX + i / N * width;
      const y = fromX + j / N * width;

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
      c.fillRect(xToC(x), yToC(y), pixelSize, pixelSize);
    }
  }
}

function mergeSplit(c, pD) {
  const f = 1;
  for (let i = 0; i < N; i++) {
    for (let j = 0; j < N; j++) {
      const x1 = fromX + i / N * width;
      const x2 = fromX + j / N * width;
      const y1 = Math.sin(f * x1 + pD) + Math.cos(f * x2 + pD);
      const y2 = Math.sin(f * -x1 + pD) + Math.cos(f * -x2 + pD);
      const intensity = 128 * y1 + 128 * y2;
      c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
      c.fillRect(xToC(x1), yToC(x2), pixelSize, pixelSize);
    }
  }
}

function pixelWaves(c, phase) {
  c.fillStyle = 'black';
  for (let i = 0; i <= N; i++) {
    const d = i / N * width;
    const x = fromX + d;
    const y = Math.sin(d);
    c.fillRect(xToC(x), yToC(y), pixelSize, pixelSize);
    c.fillRect(xToC(y), yToC(x), pixelSize, pixelSize);
  }
}

function sinX(c, phase, offset) {
  offset = offset || 0;
  begin(c);
  for (let i = 0; i <= N; i++) {
    const d = i / N * width;
    const x = fromX + d;
    const y = Math.sin(offset + x + phase);
    c.lineTo(xToC(x), yToC(y));
  }
  end(c);
}

function sinY(c, phase, offset) {
  offset = offset || 0;
  begin(c);
  for (let i = 0; i <= N; i++) {
    const d = i / N * width;
    const x = fromX + d;
    const y = Math.sin(offset + x + phase);
    c.lineTo(xToC(y), yToC(x));
  }
  end(c);
}

function axes(c) {
  // X-axis
  line(c,  fromX,   0,   width,   0);
  // Y-axis
  line(c,   0,  fromX,   0,   width);
}

function clear(c) {
  c.fillStyle = 'lightgreen';
  c.fillRect(0, 0, xToC(Tau * 2), yToC(Tau * 2));
}

function line(c, x1, y1, x2, y2) {
  begin(c);
  c.moveTo(xToC(x1), yToC(y1));
  c.lineTo(xToC(x2), yToC(y2));
  end(c);
}

function xToC(x) {
  return x / (Tau * 2) + 1;
}

function yToC(y) {
  return y / (Tau * 2) + 1;
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