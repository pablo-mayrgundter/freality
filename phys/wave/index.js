import {getHashParams, setHashParams} from '/net/web/hashparams.js';

let c;
let i = 0;
const N = 128, Pi = Math.PI, Tau = 2 * Pi;
const fromX = -2 * Tau;
const width = 4 * Tau;
const pixelSize = 0.02;
let op;

export function init() {
  const canvas = document.getElementById('canvas');
  const size = Math.min(window.innerWidth, window.innerHeight);
  canvas.width = size;
  canvas.height = size;
  c = canvas.getContext('2d');
  let xS = canvas.width / 2, yS = canvas.height / 2;
  c.scale(xS, yS);
  const params = getHashParams();
  if (params) {
    op = new RandomWaves();
    for (i in params) {
      op[i] = params[i];
    }
    anim();
  } else {
    c.font = '0.25px Arial';
    c.fillStyle = 'blue';
    c.textAlign = 'center';
    c.fillText('Click me', 1, 1);
  }
  document.getElementById('canvas').onclick = opInit;
}

function opInit() {
  const first = op == null;
  //axes(c);
  //op = new SinX().anim();
  //op = new PixelWaves().anim();
  //op = new MergeSplit().anim();
  op = new RandomWaves();
  setHashParams(op);
  //op = new RandomWaves2().anim();
  if (first) {
    anim();
  }
}

function anim() {
  op.anim();
  window.requestAnimationFrame(anim);
}

class Op {
  constructor() {
    this.c1 = r1(); this.c2 = r1(); this.c3 = r1(); this.c4 = r1();
    this.c5 = r1(); this.c6 = r1(); this.c7 = r1(); this.c8 = r1();
    this.f1 = r1(); this.f2 = r1(this.f1); this.f3 = r1(this.f2);
    this.f4 = r1(this.f3); this.f5 = r1(this.f4); this.f6 = r1(this.f5);
    this.i1 = r1(64); this.i2 = r1(64); this.i3 = r1(64); this.i4 = r1(64);
    this.i = 0;
    c.fillStyle = 'black';
  }

  anim() {
    const phaseDelta = this.i++ / (N / 8);
    const fill = c.fillStyle;
    clear(c);
    c.fillStyle = fill;
    this.run(c, phaseDelta);
  }

  run(c, pD) {}
}

class RandomWaves2 extends Op {
  run(c, pD) {
    for (let i = 0; i < N; i++) {
      for (let j = 0; j < N; j++) {
        const x = fromX + i / N * width;
        const y = fromX + j / N * width;

        const x1 = this.c1 * Math.cos(x + pD + this.f2);
        const y1 = this.c2 * Math.sin(y + pD + this.f1);

        const x2 = x1 + this.c3 * Math.cos(pD + this.f3);
        const y2 = y1 + this.c4 * Math.sin(pD + this.f4);

        const x3 = x2 + this.c5 * Math.cos(pD + this.f5);
        const y3 = y2 + this.c6 * Math.sin(pD + this.f6);

        const a1 = x1 + y1;
        const a2 = x2 + y2;
        const a3 = x3 + y3;

        const r = this.i2 * a2 + this.i1 * a1;
        const g = this.i2 * a3 + this.i1 * a2;
        const b = this.i2 * a1 + this.i1 * a3;
        c.fillStyle = `rgb(${r}, ${g}, ${b})`;
        c.fillRect(xToC(x), yToC(y), pixelSize, pixelSize);
      }
    }
  }
}

class RandomWaves extends Op {
  run(c, pD) {
    for (let i = 0; i < N; i++) {
      for (let j = 0; j < N; j++) {
        const x = fromX + i / N * width;
        const y = fromX + j / N * width;

        const x1 = x + this.c1 * Math.cos(this.f2 * x + pD);
        const y1 = y + this.c2 * Math.sin(this.f2 * y + pD);

        const x2 = x1 + this.c3 * Math.cos(this.f3 * x1);
        const y2 = y1 + this.c4 * Math.sin(this.f3 * y1);

        const x3 = x2 + this.c5 * Math.cos(this.f4 * x2);
        const y3 = y2 + this.c6 * Math.sin(this.f4 * y2);

        const x4 = x3 + this.c7 * Math.cos(this.f5 * x3);
        const y4 = y3 + this.c8 * Math.sin(this.f5 * y3);

        const a1 = x1 + y1;
        const a2 = x2 + y2;
        const a3 = x3 + y3;
        const a4 = x4 + y4;

        const r = this.i3 * a4 + this.i2 * a3 + this.i1 * a2;
        const g = this.i2 * a4 + this.i1 * a3 + this.i3 * a2;
        const b = this.i1 * a4 + this.i3 * a3 + this.i2 * a2;
        c.fillStyle = `rgb(${r}, ${g}, ${b})`;
        c.fillRect(xToC(x), yToC(y), pixelSize, pixelSize);
      }
    }
  }
}

class MergeSplit extends Op {
  run(c, pD) {
    const f = 1;
    for (let i = 0; i < N; i++) {
      for (let j = 0; j < N; j++) {
        const x1 = fromX + i / N * width;
        const x2 = fromX + j / N * width;
        const y1 = Math.cos(f * x1 + pD) + Math.sin(f * x2 + pD);
        const y2 = Math.cos(f * -x1 + pD) + Math.sin(f * -x2 + pD);
        const intensity = 128 * y1 + 128 * y2;
        c.fillStyle = `rgb(${intensity}, ${intensity}, ${intensity})`;
        c.fillRect(xToC(x1), yToC(x2), pixelSize, pixelSize);
      }
    }
  }
}

class PixelWaves extends Op {
  run(c, phase) {
    for (let i = 0; i <= N; i++) {
      const d = i / N * width;
      const x = fromX + d;
      const y = Math.sin(d + phase);
      c.fillRect(xToC(x), yToC(y), pixelSize, pixelSize);
      c.fillRect(xToC(y), yToC(x), pixelSize, pixelSize);
    }
  }
}

class SinX extends Op {
  run(c, phase, offset) {
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
}

class SinY extends Op {
  run(c, phase, offset) {
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
}

function axes(c) {
  // X-axis
  line(c,  fromX,   0,   width,   0);
  // Y-axis
  line(c,   0,  fromX,   0,   width);
}

function clear(c) {
  c.fillStyle = 'white';
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

function r1(max) {
  max = max || Tau;
  return 2 * max * Math.random() - max;
}

init();
