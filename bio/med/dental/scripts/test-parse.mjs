import { readFile } from 'node:fs/promises';
import { extractDentalScene, parseADF } from '../src/adf-parser.js';
import { MESH_KIND_CROWN, parseMeshSidecar } from '../src/mesh-sidecar.js';

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
// Decoded crown surfaces (tools/mts/build_meshes.py) must match each tooth's
// stream-header bbox and form closed genus-0 meshes (V - E + F = 2).
const meshes = parseMeshSidecar(await readFile(new URL('../PM.meshes.bin', import.meta.url)));
const crowns = new Map(meshes.filter((m) => m.kind === MESH_KIND_CROWN).map((m) => [m.toothId, m]));
for (const t of scene.teeth) {
  const m = crowns.get(t.id);
  if (!m) throw new Error(`${t.name}: no decoded crown in PM.meshes.bin`);
  const nv = m.positions.length / 3;
  const nf = m.indices.length / 3;
  for (let a = 0; a < 3; a++) {
    if (Math.abs(m.min[a] - t.meshBounds.min[a]) > 1e-6 || Math.abs(m.max[a] - t.meshBounds.max[a]) > 1e-6) {
      throw new Error(`${t.name}: decoded crown bounds differ from the stream header`);
    }
  }
  for (const i of m.indices) if (i >= nv) throw new Error(`${t.name}: face index ${i} out of range`);
  if (nv - (nf * 3) / 2 + nf !== 2) throw new Error(`${t.name}: crown is not a closed genus-0 surface`);
}
console.log('decoded crowns', crowns.size, 'verts', meshes.reduce((n, m) => n + m.positions.length / 3, 0));
console.log('ok');
