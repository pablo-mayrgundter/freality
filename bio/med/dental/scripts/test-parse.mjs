import { readFile } from 'node:fs/promises';
import { extractDentalScene, parseADF } from '../src/adf-parser.js';

const buf = await readFile(new URL('../PM.adf', import.meta.url));
const parsed = parseADF(buf);
const scene = extractDentalScene(parsed);

const u = scene.upper;
const l = scene.lower;
console.log('version', parsed.version);
console.log('upper', u?.name, 'teeth', u?.teeth.length, 'lab', u?.gingiva.labial.length, 'lin', u?.gingiva.lingual.length);
console.log('lower', l?.name, 'teeth', l?.teeth.length, 'lab', l?.gingiva.labial.length, 'lin', l?.gingiva.lingual.length);
for (const t of scene.teeth) {
  console.log(
    t.name.padEnd(10),
    t.kind.padEnd(8),
    'w',
    (t.width * 1000).toFixed(2) + 'mm',
    'd',
    (t.depth * 1000).toFixed(2) + 'mm',
    'h',
    (t.height * 1000).toFixed(2) + 'mm',
    'facc',
    t.pickPoints.length,
    'cej',
    t.cejPoints.length,
    'ip',
    t.ipPoints.map((p) => p.length).join('+') || '0',
    'mesh',
    t.hasCompressedMesh ? `${t.compressedMesh.codec}:${(t.compressedMesh.bytes / 1024).toFixed(0)}k` : 'no',
    'hintV',
    t.hintedVertexCount,
    'bounds',
    t.meshBounds
      ? t.meshBounds.max.map((v, i) => ((v - t.meshBounds.min[i]) * 1000).toFixed(1)).join('x') + 'mm'
      : 'none',
  );
}
if (scene.teeth.length < 20) {
  throw new Error(`expected ~27 teeth, got ${scene.teeth.length}`);
}
// The mts header bbox must enclose the tooth's own uncompressed overlay points.
for (const t of scene.teeth) {
  const b = t.meshBounds;
  if (!b) throw new Error(`${t.name}: no mesh bounds decoded from CompressedQedge`);
  const pts = [...t.pickPoints, ...t.cejPoints, ...t.ipPoints.flat()];
  for (const p of pts) {
    for (let a = 0; a < 3; a++) {
      if (p[a] < b.min[a] - 2e-4 || p[a] > b.max[a] + 2e-4) {
        throw new Error(`${t.name}: point ${p} outside decoded mesh bounds`);
      }
    }
  }
}
console.log('ok');
