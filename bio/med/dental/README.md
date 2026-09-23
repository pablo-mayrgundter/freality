# Align ADF → Three.js

Work log and handoff for `PM.adf`, a dentist 3D scan in Align Technology’s proprietary ClinCheck **ADF** format.

## What this is

`PM.adf` (1.8 MB) is an **AlignDataFile (bin) Version 1.1** treatment-planning file from Invisalign / ClinCheck. It is **not** STL, PLY, OBJ, or JSON. It looks like nested `{braces}` with ASCII keys, but values are length-prefixed binary blobs (floats, ints, strings, nested objects, compressed meshes).

This repo has a working **binary parser**, a **Three.js loader**, and a **viewer**. The tooth surfaces are MetaStream progressive meshes. An offline tool (`tools/mts/`) decodes them into `PM.meshes.bin`, and the viewer draws those **real crowns** plus the scan overlays (FACC curves, feature polylines, interproximal sample points). The browser can't decode meshes by itself yet, so an `.adf` opened without its `.meshes.bin` falls back to posed crown *proxies*.

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
**Load sample scan** in the HUD to pull `PM.adf` and `PM.meshes.bin`, or open / drag-drop an `.adf`
of your own (together with its `.meshes.bin`, if you've built one). Nothing is uploaded — parsing
happens entirely in the page.

```js
import { ADFLoader } from './src/ADFLoader.js';

const [adf, meshes] = await Promise.all(
  ['PM.adf', 'PM.meshes.bin'].map((u) => fetch(u).then((r) => r.arrayBuffer())),
);
const { group, teeth, scene } = new ADFLoader().parse(adf, { meshes }); // meshes optional
myScene.add(group);
```

`three` is loaded from a CDN import map in `index.html` (r170). The parser itself has no Three.js dependency.

## Layout

| Path | Role |
|---|---|
| `PM.adf` | Source scan / ClinCheck case |
| `PM.meshes.bin` | Decoded crown surfaces for `PM.adf` (built by `tools/mts/`) |
| `src/adf-parser.js` | Binary ADF parser + dental scene extract |
| `src/mesh-sidecar.js` | Reader for `*.meshes.bin` |
| `tools/mts/` | Offline MetaStream mesh decoder (Python + Unicorn + Viewpoint's DLL) |
| `src/ADFLoader.js` | `THREE.Loader` → Group with jaws/teeth/overlays |
| `index.html` / `viewer.js` | Orbit viewer, toggles, click-to-select |
| `scripts/test-parse.mjs` | Node smoke test against `PM.adf` |
| `package.json` | `"type": "module"`, `test` / `start` scripts |

## Results on this file

Parsed cleanly, consumes the whole file.

- **Upper jaw:** 13 teeth (`Tooth_02`–`Tooth_14`), 15 labial + 15 lingual gingival CVs
- **Lower jaw:** 14 teeth (`Tooth_18`–`Tooth_31`), 16+16 gingival CVs
- Missing wisdoms / a couple of 2nd molars (typical Align numbering: 1–16 upper, 17–32 lower)
- Every tooth has two `CompressedQedge` blobs (crown + initial shape), all 54 decoded
- No photographs, textures, UVs, or vertex colors in this ADF

Click a tooth in the viewer: the HUD shows e.g. `Tooth_08 (#8, incisor) — decoded surface, 3016 verts, 8.8 × 8.6 × 12.6 mm, from 21 KB mts`.

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
- **Crown surfaces** from `PM.meshes.bin`, drawn in jaw space with no extra pose
- **Real mesh bounds** (opt-in): each tooth's axis-aligned bounding box, decoded from its `CompressedQedge` header

**Placeholders (only when no `.meshes.bin` is loaded)**

- Parametric crowns (`createToothGeometry`) sized from `CrownDimensions` or FACC widths / `MinToothHeight`, posed from FACC
- Kind from Align id (molar / premolar / canine / incisor)

Scene scale: **×1000 → millimetres**. Group is rotated `X = -π/2` so occlusal +Z becomes up.

## Compressed tooth meshes (decoded offline)

Each tooth has two `CompressedData` blobs, both in jaw space (metres):

| Path | Here | Notes |
|---|---|---|
| `Tooth.CompressedQedge.CompressedData` | 13–34 KB, 1.9k–4.8k verts | The crown the viewer draws. |
| `Tooth.QedgeToothDesigner.InitialToothShape.CompressedQedge.CompressedData` | 22–53 KB, 3.2k–7.6k verts | Taller: `minZ` is 2–3 mm lower. Holds the `newvtx*` oracle. |

**Format.** These are **MetaStream 3 / Viewpoint VET** streams, the 1998–2000 MetaCreations / Real Time Geometry progressive-mesh format.
Okino's example [`creature2legs.mts`](https://www.okino.com/conv/mts_examples/creature2legs.mts) has the same container, chunk and header layout.
The stream is a chunked container (varint-sized chunks with typed ids).
The `mesh` type's concatenated payload is an LSB-first bitstream for `rtg2`'s progressive "UMF" quad-edge mesh decoder.
That bitstream holds a header, attribute plug-ins, a range-coded base mesh, then vertex-split records.
[`tools/mts/README.md`](tools/mts/README.md) has the full map.

**How it's decoded.** [`tools/mts/`](tools/mts/) runs Viewpoint's own `Mts3Reader.dll` (Media Player 3.0.15.12, not committed) under the Unicorn CPU emulator:

```bash
cd tools/mts && python3 -m venv .venv && .venv/bin/pip install -r requirements.txt
.venv/bin/python build_meshes.py ../../PM.adf /path/to/Mts3Reader.dll   # -> PM.meshes.bin
```

The output `PM.meshes.bin` (1.4 MB, 27 crowns, 80k vertices) is committed. The viewer loads it next to `PM.adf`.

**Validation.**
- All 54 blobs decode to within the last byte of their stream.
- Every surface is closed and genus 0.
- Decoded bounds equal the stream-header bboxes to within 1 nm.
- All 354 plain-text `newvtxData` points of tooth 2 have a decoded vertex within 0.12 mm.
  Align's `newvtxIndices` are a different numbering of the same vertices, not the decoded vertex order.

**Without the DLL.** `parseMtsHeader()` in `src/adf-parser.js` still reads each crown's bounding box straight from the bitstream.
That's what the **Real mesh bounds** overlay draws for ADFs that have no sidecar.

**Earlier dead ends.** Byte-level codecs (zlib, LZMA, zstd, …), raw and quantised float searches, and fixed-width field guesses all failed.
The reason: the stream is bit-packed *and* split into ~510-byte chunks, with range-coded geometry.
The `Fbits0…7` names turned out to be strings inside plug-in headers, not codec tags.
`QedgeA` / `QedgeB` wasn't a class variant either: the letter is the first byte of the next chunk's length varint.

## No image overlay in this ADF

Scanned for JPEG/PNG/GIF/WebP/BMP/TIFF/DDS and for Texture/UV/Photo/Bitmap/vertexColor fields.

- Three `FF D8 FF` hits are accidental bytes inside compressed meshes; PIL cannot open them.
- `d_IO_segmentColors` is a tiny list of tooth-segment **ids**, not RGB.
- `Bite+from+IntraOral+scan` is a boolean.
- File is far too small for an iTero color/NIRI textured arch.

Dentist screens that looked like a photo on a 3D model almost certainly came from **another export** (iTero PLY/OBJ/project, portal JPEGs, or ClinCheck’s own shaded renderer). Ask the user for those if the goal is a textured scan.

## Suggested next work (priority)

1. **Port the decoder to JavaScript** so the viewer can open any ADF without a prebuilt sidecar. The emulator in `tools/mts/` is the reference. Port the bit reader, the range decoder and its models, the base mesh, the vertex splits and the plug-ins one at a time, diffing each against the emulator's memory at the same point.
2. Optionally show the `InitialToothShape` meshes (`build_meshes.py --initial`), e.g. as a toggle.
3. Work out how Align's `newvtxIndices` numbering maps onto the decoded vertex order, if the IP data should attach to the surface.
4. Do **not** spend time on textures unless a second file shows up.

## Implementation notes for the next agent

- Parser is recursive and already walks the whole file. Prefer extending `extractTooth` / `describeCompressedMesh` rather than rewriting the container parser.
- `decodeValue()` returns tagged objects (`{vec3}`, `{i32,f32}`, `{points}`, `{bytes,size}`). Helpers: `vec3()`, `i32()`, `f32()`, `str()`, `asList()`, `transformOf()`, `collectNamed()`.
- Duplicate field names become arrays (`Tooth`, `CVHandler`, `Vector3<float>+>`).
- `newvtxIndices` max id is a **lower bound** on vertex count (there may be unused ids).
- Gingiva loft is a filled planar strip; default HUD has it **off**. Lines are still built.
- Viewer raycast only hits `*_crown` meshes; `userData` on the tooth group holds `compressedMesh`, `hintedVertexCount`, `sampledVertexCount`, `meshBounds`, and (with a sidecar) `realMesh` / `vertexCount`.
- `parseMtsHeader(bytes)` (exported from `adf-parser.js`) returns `{ variant, bbox: {min, max} }` for a `CompressedData` blob.
- `ADFLoader.parse(buffer, { meshes })` takes a `*.meshes.bin` ArrayBuffer (or parsed entries). Real crowns go in the tooth group as `<Tooth>_crown`, and the group's `userData.realMesh` is set.
- `scripts/test-parse.mjs` checks every decoded crown against its header bbox and requires a closed genus-0 surface.

## Open questions

- Whether `InitialToothShape` is the unposed rest shape or just a taller, untrimmed version of the crown. Its x/y extents match the crown's.
- What the header's `flags` bits and the `Fbits[i] = 6` plug-in values control.
- Companion iTero color files for this patient, if they exist outside this directory.
