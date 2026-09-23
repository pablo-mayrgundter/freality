# Align ADF → Three.js

Work log and handoff for `PM.adf`, a dentist 3D scan in Align Technology’s proprietary ClinCheck **ADF** format.

## What this is

`PM.adf` (1.8 MB) is an **AlignDataFile (bin) Version 1.1** treatment-planning file from Invisalign / ClinCheck. It is **not** STL, PLY, OBJ, or JSON. It looks like nested `{braces}` with ASCII keys, but values are length-prefixed binary blobs (floats, ints, strings, nested objects, compressed meshes).

This repo has a working **binary parser**, a **Three.js loader**, and a **viewer**. The full-resolution tooth surfaces are present in the file but still compressed with an undocumented codec, so the viewer shows posed crown *proxies* plus real scan overlays (FACC curves, feature polylines, interproximal sample points).

## Live demo

<https://pablo-mayrgundter.github.io/freality/bio/med/dental/>

Served straight off `main` by GitHub Pages. PRs that touch this directory get their own
preview at `…/freality/pr-preview/pr-<N>/` (see `.github/workflows/pr-preview.yml`).

## Quick start

```bash
node scripts/test-parse.mjs          # parse PM.adf, print 27 teeth
python3 -m http.server 8765          # or: npm start
# open http://127.0.0.1:8765/
```

Needs a local HTTP server (ES modules + fetch of `PM.adf`). The viewer starts empty: hit
**Load sample scan** in the HUD to pull `PM.adf`, or open / drag-drop an `.adf` of your own.
Nothing is uploaded — parsing happens entirely in the page.

```js
import { ADFLoader } from './src/ADFLoader.js';

const loader = new ADFLoader();
const { group, teeth, scene } = await loader.loadAsync('PM.adf');
myScene.add(group);
```

`three` is loaded from a CDN import map in `index.html` (r170). The parser itself has no Three.js dependency.

## Layout

| Path | Role |
|---|---|
| `PM.adf` | Source scan / ClinCheck case |
| `src/adf-parser.js` | Binary ADF parser + dental scene extract |
| `src/ADFLoader.js` | `THREE.Loader` → Group with jaws/teeth/overlays |
| `index.html` / `viewer.js` | Orbit viewer, toggles, click-to-select |
| `scripts/test-parse.mjs` | Node smoke test against `PM.adf` |
| `package.json` | `"type": "module"`, `test` / `start` scripts |

## Results on this file

Parsed cleanly, consumes the whole file.

- **Upper jaw:** 13 teeth (`Tooth_02`–`Tooth_14`), 15 labial + 15 lingual gingival CVs
- **Lower jaw:** 14 teeth (`Tooth_18`–`Tooth_31`), 16+16 gingival CVs
- Missing wisdoms / a couple of 2nd molars (typical Align numbering: 1–16 upper, 17–32 lower)
- Every tooth has a `CompressedQedge` blob (real mesh, not decoded)
- No photographs, textures, UVs, or vertex colors in this ADF

Click a tooth in the viewer: HUD shows e.g. `real mesh 29 KB Fbits3, ~3727 verts (codec not decoded yet)`.

## ADF container format (solved)

Text header, then one root object:

```
AlignDataFile ( bin )\nVersion 1.1\n\n
{ fieldName <uint32le size> <payload> fieldName <size> <payload> ... }
```

- `{` / `}` delimit objects. Repeated keys (many `Tooth` fields) are arrays.
- Field names: `[A-Za-z0-9+._\-:/<>*?#]+` then a space. C++ template names appear (`Vector3<float>+>+>+>`).
- Payload is either nested `{...}`, a sequence of objects, an unwrapped field body (no braces), or a primitive:
  - 0 bytes empty, 1 byte bool/u8, 4 bytes i32+f32, 12 bytes vec3, 16 bytes vec4/quat
  - printable ASCII → string (often trailing space, e.g. `Upper+Jaw `)
  - `uint32 count` + `count` × float32[3] → point array
  - `uint32 count` + `count` × float64[3] → CEJ points
  - `uint32 count` + `count` × int32 → index array (`newvtxIndices`)
- Some values have a **12-byte zero prefix** then `{...}` (skip it).
- Coordinates are **metres**. Quaternions are **(w, x, y, z)**; identity is `(1,0,0,0)`.
- `CrownDimensions` / `Feature:CrownDimensions` is `[buccal-lingual, mesial-distal, occlusal height]` in metres.

Root shape:

```
JawPair
  upper / lower
    Transform, Name, basis, Dentition, Tooth…, GingivaSurface, CompoundSpline, CompressedQedge-related, VC2, …
ParamObject, AlgorithmSettingsObj, FiPosDocumentContainer_V21, CaseSetup, …
```

Each `Tooth` has `id`, `Name`, `animXfm` (translation + rotation + `crownCenter` / `rootCenter`), `FACCCurve`, `QedgeToothDesigner` (includes `InitialToothShape` with another compressed mesh), `CEJPoints`, feature `Vector3<float>…` objects, and top-level `CompressedQedge`.

## What the viewer draws

**Real data from the file**

- Per-tooth pose from FACC: +Z = `FALineDir` (flipped to point toward `crownCenter`), +Y = `FAPointNormal`, origin = `animXfm.translation`
- FACC pick polylines (~22–23 pts/tooth)
- Feature curves: IncisalRidge, BuccalCusps, LingualCusps, MolarGroove, TipPoint, …
- Interproximal sample clouds (`newvtxData`, up to a few hundred verts/tooth)
- Gingival Lab+/Lin+ spline CVs (planar horseshoe; loft is opt-in and looks like a slab from some cameras)

**Placeholders (not the scan surface)**

- Parametric crowns (`createToothGeometry`) sized from `CrownDimensions` or FACC widths / `MinToothHeight`
- Kind from Align id (molar / premolar / canine / incisor)

Scene scale: **×1000 → millimetres**. Group is rotated `X = -π/2` so occlusal +Z becomes up.

## Compressed tooth meshes (unsolved — main next job)

These **are** the surfaces that should replace the ivory proxies.

### Where

`Tooth.CompressedQedge.CompressedData` (high-res) and a second blob under `QedgeToothDesigner.InitialToothShape` (often another resolution). **54** `CompressedData` blobs in `PM.adf` (2 per tooth).

`QedgeRes` is `quadEdgeHigh`. This is Align’s **quad-edge** mesh (`Qedge`), not a public triangle format.

### Typical sizes on this case

| Kind | Codec tag | Packed size | Hinted vertex count (`max(newvtxIndices)+1`) |
|---|---|---|---|
| Molars | `Fbits3` | ~29–33 KB | ~3,000–4,300 |
| Premolars | `Fbits0` | ~16–19 KB | ~2,000–2,600 |
| Incisors / canines | `Fbits0` or `Fbits5` | ~13–21 KB | ~4,000–6,000 |

Example: `Tooth_02` → 29 KB `Fbits3`, hinted **3727** verts. `newvtxData` holds **354** of those verts in the clear; `newvtxIndices` is `{997,998,999,1107,…,3726}` — IDs into the *full* mesh, not a local 0..353 range.

### `"mts` / Fbits bitstream (partial map)

`CompressedData` payload:

```
uint32le innerSize          # remaining bytes
"mts" \0 \0 \0 \0           # magic + version 0  (bytes: 22 6d 74 73 00 00 00 00)
$$  uint16le 4  uint32le 1  # typed chunk
A?  uint16le 6  uint16le 4 "mesh"
5 zero bytes + flag byte:
  0xA0 → Fbits3#   (26 blobs)
  0x80 → Fbits0#   (24 blobs)
  0x60 → Fbits5#   (4 blobs)
then ~variable header~
then a long SHARED codec preamble (identical across same Fbits version)
then entropy-coded body
```

Fbits3 shared tail includes ASCII `Fbits3#` then `\xb1 34 ba 39 …`. First byte that differs across Fbits3 blobs is around offset **136**. Inside the body, plaintext names `dir`, `mesh`, `QedgeA` appear (type directory / serializer residue).

`"mts` matches the 1998 MetaStream carrier mnemonic (`"mts"`), but this is almost certainly Align-specific serialization of a Qedge, not a stock MetaStream reader.

### Decode attempts that failed

Tried on first-tooth blob (`CompressedData` inner, 38319 bytes):

- zlib / deflate (all wbits), lz4 frame+block, zstd, brotli, bz2
- LZMA raw / alone, including 5-byte props at the `\x83 30 26 50 11` “codec sig”
- Raw float32 / float64 of known `newvtxData` points — **not present** in the packed bytes
- Naïve uint16 dequantization into several bboxes — no hits
- OpenCTM / Draco magics — absent

Conclusion: custom **range / arithmetic coder** (`Fbits`) after a shared model init, not a wrapper around a public codec.

### Oracle for a future decoder

Ground truth for `Tooth_02` (and the same pattern on other teeth):

1. Parse `newvtxData`: `uint32 n` + `n` × `float32 x,y,z` (metres, same space as FACC / translation).
2. Parse `newvtxIndices`: `uint32 n` + `n` × `uint32` full-mesh vertex ids.
3. After decompress, `vertices[index[i]]` must match `newvtxData[i]` (possibly after the tooth basis transform — try both jaw space and local).

First IP vert on tooth 02: `(-0.023489, -0.014045, -0.002258)` at mesh id **997**.

A decoder that cannot hit those points is wrong.

## No image overlay in this ADF

Scanned for JPEG/PNG/GIF/WebP/BMP/TIFF/DDS and for Texture/UV/Photo/Bitmap/vertexColor fields.

- Three `FF D8 FF` hits are accidental bytes inside compressed meshes; PIL cannot open them.
- `d_IO_segmentColors` is a tiny list of tooth-segment **ids**, not RGB.
- `Bite+from+IntraOral+scan` is a boolean.
- File is far too small for an iTero color/NIRI textured arch.

Dentist screens that looked like a photo on a 3D model almost certainly came from **another export** (iTero PLY/OBJ/project, portal JPEGs, or ClinCheck’s own shaded renderer). Ask the user for those if the goal is a textured scan.

## Suggested next work (priority)

1. **Decode Fbits / Qedge** (the actual ask). Treat `"mts` as a small typed header, then implement or reverse the `Fbits3#` range coder. Shared preamble bytes are a dictionary / model init. Validate with the `newvtxData` oracle. If you get vertices, connectivity is likely a quad-edge (`QedgeA`, `dir`, `Onext`-style) — triangulate for Three.js.
2. **Swap proxies** in `ADFLoader.buildJaw`: if decode succeeds, `BufferGeometry` from verts+faces, keep the same `toothPose` (or apply `animXfm` directly once space is known). Keep FACC/feature lines as debug overlays.
3. **Confirm coordinate space** of decoded verts vs `animXfm.translation` / `crownCenter`. Proxies currently use an FACC frame, not the raw quaternion, because the quat alone posed molars on their sides.
4. Do **not** spend time on textures unless a second file shows up.

## Implementation notes for the next agent

- Parser is recursive and already walks the whole file. Prefer extending `extractTooth` / `describeCompressedMesh` rather than rewriting the container parser.
- `decodeValue()` returns tagged objects (`{vec3}`, `{i32,f32}`, `{points}`, `{bytes,size}`). Helpers: `vec3()`, `i32()`, `f32()`, `str()`, `asList()`, `transformOf()`, `collectNamed()`.
- Duplicate field names become arrays (`Tooth`, `CVHandler`, `Vector3<float>+>`).
- `newvtxIndices` max id is a **lower bound** on vertex count (there may be unused ids).
- Gingiva loft is a filled planar strip; default HUD has it **off**. Lines are still built.
- Viewer raycast only hits `*_crown` meshes; `userData` on the tooth group holds `compressedMesh`, `hintedVertexCount`, `sampledVertexCount`.

## Open questions

- Exact Fbits symbol model (likely Align-internal; patents mention ADF but not this bitstream).
- Whether `InitialToothShape.CompressedQedge` is a coarser LOD or the unposed rest shape.
- Whether a ClinCheck / Treat install on disk contains a decoder (DLL/so) worth diffing — only if the user has that software.
- Companion iTero color files for this patient, if they exist outside this directory.
