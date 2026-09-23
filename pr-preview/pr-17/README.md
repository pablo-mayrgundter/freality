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

Click a tooth in the viewer: the HUD shows e.g. `real mesh 14 KB mts, ≥4061 verts, bounds 5.6 × 6.7 × 12.3 mm (surface not decoded yet)`.

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
- **Real mesh bounds** (opt-in): each tooth's axis-aligned bounding box, decoded from its `CompressedQedge` header

**Placeholders (not the scan surface)**

- Parametric crowns (`createToothGeometry`) sized from `CrownDimensions` or FACC widths / `MinToothHeight`
- Kind from Align id (molar / premolar / canine / incisor)

Scene scale: **×1000 → millimetres**. Group is rotated `X = -π/2` so occlusal +Z becomes up.

## Compressed tooth meshes (header decoded, body unsolved — main next job)

These **are** the surfaces that should replace the ivory proxies.

### Where

**54** `CompressedData` blobs in `PM.adf`, 2 per tooth, both in the same jaw-space frame:

| Path | Size here | Notes |
|---|---|---|
| `Tooth.QedgeToothDesigner.InitialToothShape.CompressedQedge.CompressedData` | 22–53 KB | Comes first in the file. The `newvtx*` oracle below belongs to **this** mesh. |
| `Tooth.CompressedQedge.CompressedData` | 13–34 KB | Same x/y extent, but `minZ` is ~2–3 mm higher, so it's trimmed at the cervical end. |

`QedgeRes` is `quadEdgeHigh`, `PreserveFaceOrder` 0, `FaceFlags` 0, `GeomId` -1. It's Align's **quad-edge** mesh (`Qedge`), not a public triangle format.

### The stream is LSB-first bits, not bytes

The main finding from the second pass. After byte 30, a `CompressedData` blob is **one bitstream, packed least-significant-bit first**. Fields are not byte-aligned, which is why earlier byte-level searches found nothing. The last byte of all 54 blobs looks like zero-padding (50/54 are < 0x80, 11 are exactly 0x00). That points to a bit writer, not a byte-oriented range coder.

To read bit `p`: `(bytes[p >> 3] >> (p & 7)) & 1`. `readBits()` / `parseMtsHeader()` in `src/adf-parser.js` implement it. Offsets below are bit offsets from the start of the `CompressedData` value, **including** its `uint32` size prefix.

```
byte 0    uint32le innerSize
byte 4    22 6d 74 73 00 00 00 00        '"mts' + 0
byte 12   24 24 00 04 01 00 00 00        '$$' chunk
byte 20   41 XX 00 06 00 04 'mesh'       'A' chunk; XX = (byte offset of "\x03dir") - 282
bit 285   3 bits  v ∈ {3,4,5}             (26 blobs v=5, 24 v=4, 4 v=3)
bit 288   49+3v bits: three (v+7)-bit fields n+2, n, n (bits between them are
          constant per v). n is the same for teeth of the same type, e.g.
          1425 for both upper-molar InitialToothShapes and 1466 for all four
          upper premolars, but it doesn't track blob size. Meaning unknown.
          Then a constant 89-bit run.
bit 441-3(5-v)   6 × float32  BOUNDING BOX  minX minY minZ maxX maxY maxZ   (metres, jaw space)
          +106 bits of flags (nearly constant across blobs)
          8 × { "Fbits" (5 × 8-bit chars) · 3-bit index 0..7 · 4-bit value (6 in every blob) }
          ~1.4–1.9 kbit data-dependent table (three similar sub-blocks, each
          starting with ~4 items shaped 00 7E/7F xx; plausibly per-axis model
          or code tables)
byte-aligned  ?? 15 01 05 00 03'dir' 01 04'mesh' 01 00 05'Qedge' ('A'|'B')
          body (the rest of the blob): the mesh itself, still undecoded
```

**Bounding box: solved and verified.** For all 27 teeth, every FACC pick point, CEJ point and interproximal `newvtxData` point lies inside the decoded box of that tooth's blob. `scripts/test-parse.mjs` asserts this. The loader exposes it as `tooth.meshBounds` and draws it as the **Real mesh bounds** overlay.

The earlier `Fbits0/3/5` "codec tags" were an artifact. The same 8-entry `Fbits0#…Fbits7#` table is in **every** blob, but it's shifted 0/3/6 bits depending on `v`, so a byte-aligned scan only ever caught one entry. It isn't a codec version. The shared-preamble bytes (`83 30 26 50 …`) are the same constant bits seen at different shifts.

### The body

- First ~2–3 KB: long runs of 0s and 1s (up to ~80 bits). These thin out gradually. From about 20 kbit on, the stream is statistically indistinguishable from random: byte entropy 7.99, no bit autocorrelation at lags 1–260, and zlib/xz can't shrink it.
- That profile fits an **adaptive** entropy coder that starts untrained: adaptive Rice/Golomb, or a bit-output arithmetic coder coding long streaks of the same symbol. It could also be adaptive-width sign-extended integers, which would explain the `ff ff ff` runs near the start. It is **not** fixed-width fields.
- Meshes that probably share a template, like the four upper-premolar InitialToothShapes (24.6 KB ± 20 bytes), share no common prefix in the body. Geometry and connectivity are therefore interleaved, or geometry comes first.
- The 354 oracle vertex ids run 997–999, 1107–1112, 1223–1230, 1341–… in rows of ~110–118. That ordering looks like a region-growing / ring traversal (Touma–Gotsman or Edgebreaker style), not a scan-line grid.
- Budget: roughly 75–80 bits per vertex, measured against `max(newvtxIndices)+1`. That's a lot. Geometry is probably fine-quantized (≈20+ bits per vertex per axis before entropy coding).

### Decode attempts that failed

Byte-level (first pass):

- zlib / deflate (all wbits), lz4 frame+block, zstd, brotli, bz2, LZMA raw / alone
- Raw float32 / float64 of known `newvtxData` points — **not present**
- OpenCTM / Draco magics — absent

Bit-level (second pass):

- Quantised oracle vertices, relative to the decoded bbox (per-axis and cube), at 6–24 bits, interleaved xyz **and** planar x…/y…/z… at every bit offset: no hits above chance
- Truncated float32 (sign + exponent + top 6–12 mantissa bits) of oracle coords at every bit offset: exactly chance
- `[width][value]` self-delimiting integer parses of the header (2–6-bit width prefixes, every start offset): no parse that lands consistently on the next known field
- Reduced-precision float layouts for the table region: no consistent chain

### MetaStream lead

`"mts` is very likely **MetaStream** (MetaCreations / Real Time Geometry with Intel, 1998–99; licensed into DirectX 6). It's described as a chunked, multiresolution mesh format using *quantization, predictive coding and adaptive entropy coding*, with progressive vertex-split streams. That matches everything above.

The primary sources are Abadjev, del Rosario, Lebedev, Migdal, Paskhaver, "MetaStream", *VRML '99*, pp. 53–62, and the RTG/MetaCreations patents. None of them were reachable from the environment this pass ran in; that's the best next read.

### Oracle for a future decoder

Ground truth for `Tooth_02` (and the same pattern on other teeth), under `InitialToothShape.InitialIPPositions`:

1. Parse `newvtxData`: `uint32 n` + `n` × `float32 x,y,z` (metres, same jaw space as FACC and the decoded bbox).
2. Parse `newvtxIndices`: `uint32 n` + `n` × `uint32` full-mesh vertex ids (`newvtxCounters` are all 1; `newDataSizes` = `[1, n]`).
3. After decompressing the **InitialToothShape** blob, `vertices[index[i]]` must match `newvtxData[i]`.

First IP vert on tooth 02: `(-0.023489, -0.014045, -0.002258)` at mesh id **997**. Its bbox is `(-31.40, -23.41, -7.83)…(-20.16, -12.07, 5.21)` mm.

A decoder that cannot hit those points is wrong.

## No image overlay in this ADF

Scanned for JPEG/PNG/GIF/WebP/BMP/TIFF/DDS and for Texture/UV/Photo/Bitmap/vertexColor fields.

- Three `FF D8 FF` hits are accidental bytes inside compressed meshes; PIL cannot open them.
- `d_IO_segmentColors` is a tiny list of tooth-segment **ids**, not RGB.
- `Bite+from+IntraOral+scan` is a boolean.
- File is far too small for an iTero color/NIRI textured arch.

Dentist screens that looked like a photo on a 3D model almost certainly came from **another export** (iTero PLY/OBJ/project, portal JPEGs, or ClinCheck’s own shaded renderer). Ask the user for those if the goal is a textured scan.

## Suggested next work (priority)

1. **Decode the body** (the actual ask). Read it as an LSB-first bitstream starting right after `Qedge{A,B}`. Get the MetaStream paper/patents first: the decoder design (progressive vertex splits? adaptive Rice? bit-output arithmetic coder?) is the missing piece. Validate against the `newvtxData` oracle on the **InitialToothShape** blob. If you get vertices, connectivity is likely quad-edge (`Qedge`) — triangulate for Three.js.
2. **Cheap win now:** the decoded bboxes show the proxies sit too low (they grow up from `animXfm.translation`, which sits a few mm below the real mesh's `minZ`). Fitting each proxy into its `meshBounds` would place the crowns correctly without the full decode.
3. **Swap proxies** in `ADFLoader.buildJaw`: if decode succeeds, `BufferGeometry` from verts+faces, keep the same `toothPose` (or apply `animXfm` directly once space is known). Keep FACC/feature lines as debug overlays.
4. **Confirm coordinate space** of decoded verts vs `animXfm.translation` / `crownCenter`. Proxies currently use an FACC frame, not the raw quaternion, because the quat alone posed molars on their sides.
5. Do **not** spend time on textures unless a second file shows up.

## Implementation notes for the next agent

- Parser is recursive and already walks the whole file. Prefer extending `extractTooth` / `describeCompressedMesh` rather than rewriting the container parser.
- `decodeValue()` returns tagged objects (`{vec3}`, `{i32,f32}`, `{points}`, `{bytes,size}`). Helpers: `vec3()`, `i32()`, `f32()`, `str()`, `asList()`, `transformOf()`, `collectNamed()`.
- Duplicate field names become arrays (`Tooth`, `CVHandler`, `Vector3<float>+>`).
- `newvtxIndices` max id is a **lower bound** on vertex count (there may be unused ids).
- Gingiva loft is a filled planar strip; default HUD has it **off**. Lines are still built.
- Viewer raycast only hits `*_crown` meshes; `userData` on the tooth group holds `compressedMesh`, `hintedVertexCount`, `sampledVertexCount`, `meshBounds`.
- `parseMtsHeader(bytes)` (exported from `adf-parser.js`) returns `{ variant, bbox: {min, max}, qedgeClass, bodyOffset, bodyBytes }` for a `CompressedData` blob.

## Open questions

- The body's entropy coder and symbol model (MetaStream lineage suspected, see above).
- What the three `n` header fields and the `Fbits[i] = 6` table mean (per-template quantisation / precision settings?).
- Whether `InitialToothShape.CompressedQedge` is a coarser LOD or the unposed rest shape.
- Whether a ClinCheck / Treat install on disk contains a decoder (DLL/so) worth diffing — only if the user has that software.
- Companion iTero color files for this patient, if they exist outside this directory.
