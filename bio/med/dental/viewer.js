import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { ADFLoader } from './src/ADFLoader.js';

const canvas = document.getElementById('c');
const statusEl = document.getElementById('status');
const dropEl = document.getElementById('drop');

const renderer = new THREE.WebGLRenderer({ canvas, antialias: true });
renderer.setPixelRatio(Math.min(devicePixelRatio, 2));
renderer.shadowMap.enabled = true;
renderer.toneMapping = THREE.ACESFilmicToneMapping;
renderer.toneMappingExposure = 1.05;

const scene = new THREE.Scene();
scene.background = new THREE.Color(0x1a1c1f);
scene.fog = new THREE.Fog(0x1a1c1f, 180, 420);

const camera = new THREE.PerspectiveCamera(40, 1, 0.1, 1000);
camera.position.set(0, 40, 90);

const controls = new OrbitControls(camera, canvas);
controls.enableDamping = true;
controls.target.set(0, 0, 0);

scene.add(new THREE.HemisphereLight(0xf2f0ea, 0x2a3038, 1.1));
const key = new THREE.DirectionalLight(0xffffff, 1.4);
key.position.set(40, 80, 50);
key.castShadow = true;
scene.add(key);
const fill = new THREE.DirectionalLight(0xb9d4ff, 0.45);
fill.position.set(-60, 20, -30);
scene.add(fill);

const ground = new THREE.Mesh(
  new THREE.CircleGeometry(80, 48),
  new THREE.MeshStandardMaterial({ color: 0x14161a, roughness: 1 }),
);
ground.rotation.x = -Math.PI / 2;
ground.position.y = -18;
ground.receiveShadow = true;
scene.add(ground);

let model = null;

function resize() {
  const w = canvas.clientWidth;
  const h = canvas.clientHeight;
  renderer.setSize(w, h, false);
  camera.aspect = w / Math.max(h, 1);
  camera.updateProjectionMatrix();
}

function frameObject(obj) {
  const box = new THREE.Box3().setFromObject(obj);
  const size = box.getSize(new THREE.Vector3());
  const center = box.getCenter(new THREE.Vector3());
  controls.target.copy(center);
  const span = Math.max(size.x, size.y, size.z, 40);
  camera.position.set(center.x, center.y + span * 0.35, center.z + span * 1.6);
  camera.near = span / 100;
  camera.far = span * 20;
  camera.updateProjectionMatrix();
  controls.update();
}

const raycaster = new THREE.Raycaster();
const pointer = new THREE.Vector2();
let selected = null;

canvas.addEventListener('pointerdown', (event) => {
  if (!model) return;
  const rect = canvas.getBoundingClientRect();
  pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
  raycaster.setFromCamera(pointer, camera);
  const meshes = [];
  model.group.traverse((o) => {
    if (o.isMesh && o.name.endsWith('_crown')) meshes.push(o);
  });
  const hits = raycaster.intersectObjects(meshes, false);
  if (selected) {
    selected.material.emissive?.setHex(0x000000);
    selected = null;
  }
  if (hits[0]) {
    selected = hits[0].object;
    selected.material.emissive = new THREE.Color(0x224466);
    const ud = selected.parent?.userData || {};
    const kb = ud.compressedMesh?.bytes
      ? `${(ud.compressedMesh.bytes / 1024).toFixed(0)} KB ${ud.compressedMesh.codec || 'qedge'}`
      : 'no mesh blob';
    const b = ud.meshBounds;
    const size = b
      ? `, ${b.max.map((v, i) => ((v - b.min[i]) * 1000).toFixed(1)).join(' × ')} mm`
      : '';
    const surface = ud.realMesh
      ? `decoded surface, ${ud.vertexCount} verts`
      : 'proxy crown (no decoded surface loaded)';
    statusEl.textContent = `Selected ${selected.parent?.name || ''} (#${ud.toothId}, ${ud.kind}) — ${surface}${size}, from ${kb}`;
  }
});

function bindToggles() {
  const map = [
    ['tog-upper', (m) => m.jaws.upper],
    ['tog-lower', (m) => m.jaws.lower],
    ['tog-facc', (m) => [m.jaws.upper?.userData.facc, m.jaws.lower?.userData.facc]],
    ['tog-gingiva', (m) => [m.jaws.upper?.userData.gingiva, m.jaws.lower?.userData.gingiva]],
    ['tog-scan', (m) => [m.jaws.upper?.userData.scanPoints, m.jaws.lower?.userData.scanPoints]],
    ['tog-bounds', (m) => [m.jaws.upper?.userData.meshBounds, m.jaws.lower?.userData.meshBounds]],
  ];
  for (const [id, pick] of map) {
    const el = document.getElementById(id);
    el.addEventListener('change', () => {
      if (!model) return;
      const nodes = [].concat(pick(model) || []).filter(Boolean);
      for (const n of nodes) n.visible = el.checked;
    });
  }
}

function applyToggles() {
  document.getElementById('tog-upper').dispatchEvent(new Event('change'));
  document.getElementById('tog-lower').dispatchEvent(new Event('change'));
  document.getElementById('tog-facc').dispatchEvent(new Event('change'));
  document.getElementById('tog-gingiva').dispatchEvent(new Event('change'));
  document.getElementById('tog-scan').dispatchEvent(new Event('change'));
  document.getElementById('tog-bounds').dispatchEvent(new Event('change'));
}

function showModel(result, label) {
  if (model) {
    scene.remove(model.group);
    model.group.traverse((o) => {
      if (o.geometry) o.geometry.dispose();
      if (o.material) {
        const mats = [].concat(o.material);
        for (const m of mats) m.dispose?.();
      }
    });
  }
  model = result;
  scene.add(result.group);
  const n = result.teeth.length;
  const u = result.scene.upper?.teeth.length || 0;
  const l = result.scene.lower?.teeth.length || 0;
  const real = result.group.userData.realCrowns || 0;
  statusEl.textContent = real
    ? `${label}: ${n} teeth (${u} upper, ${l} lower), ${real} decoded crown surfaces.`
    : `${label}: ${n} teeth (${u} upper, ${l} lower). Proxy crowns: add the matching .meshes.bin for real surfaces.`;
  frameObject(result.group);
  applyToggles();
}

function parseWithMeshes(buffer, meshes) {
  const result = new ADFLoader().parse(buffer, { meshes });
  let real = 0;
  result.group.traverse((o) => {
    if (o.userData.realMesh) real++;
  });
  result.group.userData.realCrowns = real;
  return result;
}

async function loadBuffer(buffer, label, meshes) {
  statusEl.textContent = `Parsing ${label}…`;
  showModel(parseWithMeshes(buffer, meshes), label);
}

async function loadUrl(url) {
  statusEl.textContent = `Fetching ${url}…`;
  const sidecar = url.replace(/\.adf$/i, '.meshes.bin');
  const [adf, meshes] = await Promise.all([
    fetch(url).then((r) => {
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      return r.arrayBuffer();
    }),
    fetch(sidecar).then((r) => (r.ok ? r.arrayBuffer() : undefined), () => undefined),
  ]);
  showModel(parseWithMeshes(adf, meshes), url);
}

/** Accept an .adf plus, optionally, its .meshes.bin sidecar. */
async function loadFiles(fileList) {
  const files = [...(fileList || [])];
  const adf = files.find((f) => /\.adf$/i.test(f.name));
  if (!adf) {
    statusEl.textContent = 'Choose an .adf file (optionally with its .meshes.bin).';
    return;
  }
  const side = files.find((f) => /\.meshes\.bin$/i.test(f.name));
  await loadBuffer(await adf.arrayBuffer(), adf.name, side ? await side.arrayBuffer() : undefined);
}

bindToggles();

const sampleEl = document.getElementById('sample');
sampleEl.addEventListener('click', async () => {
  sampleEl.disabled = true;
  try {
    await loadUrl('./PM.adf');
  } catch (err) {
    console.error(err);
    statusEl.textContent = `Could not load PM.adf (${err.message}). Use the file picker.`;
    sampleEl.disabled = false;
  }
});

document.getElementById('file').addEventListener('change', (e) => loadFiles(e.target.files));

window.addEventListener('dragover', (e) => {
  e.preventDefault();
  dropEl.classList.add('visible');
});
window.addEventListener('dragleave', () => dropEl.classList.remove('visible'));
window.addEventListener('drop', async (e) => {
  e.preventDefault();
  dropEl.classList.remove('visible');
  await loadFiles(e.dataTransfer?.files);
});

function tick() {
  resize();
  controls.update();
  renderer.render(scene, camera);
  requestAnimationFrame(tick);
}
tick();
