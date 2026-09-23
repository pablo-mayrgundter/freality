# MetaStream (.mts) tooth-mesh decoder

The `CompressedQedge.CompressedData` blobs in an ADF are **MetaStream 3** (Viewpoint
VET) geometry streams, the 1998–2000 MetaCreations / Real Time Geometry format.
This tool decodes them by running Viewpoint's own stream reader,
`Mts3Reader.dll`, under the [Unicorn](https://www.unicorn-engine.org/) CPU emulator.
It stubs only the DLL's 30 C-runtime and Win32 imports; all of the decoding is the
original x86 code.

```bash
python3 -m venv .venv && .venv/bin/pip install -r requirements.txt
.venv/bin/python build_meshes.py ../../PM.adf /path/to/Mts3Reader.dll    # -> ../../PM.meshes.bin
.venv/bin/python build_meshes.py ../../PM.adf /path/to/Mts3Reader.dll --initial --obj out/
```

A whole ADF (27 teeth) takes about 12 s. The viewer loads `<name>.meshes.bin`
next to `<name>.adf` and draws the real crowns in place of the proxies.

## The DLL

The DLL is **not in this repo** (`*.dll` is git-ignored) because it's Viewpoint's code.
It's the "Viewpoint Media Player MTS3 Reader Component", file version
**3.0.15.12** (© 2000 Viewpoint Corporation). It was downloaded from dllme.com.

```
sha256 a87b071251e6827a3c623bd81e69e0214d35c2817ef00c3691320a9d9fc0b765  Mts3Reader.dll
```

The addresses in `mts.py` are for that build. Other builds will need them found again.

## How it works

| File | Role |
|---|---|
| `emu.py` | Loads the PE image, sets up flat segments plus an FS/TEB for SEH, and stubs the imports (Python hooks, or small x86 stubs for the FPU / `_EH_prolog` / `_initterm`). |
| `mts.py` | MetaStream container parsing, and the decoder driver: factory → stock attribute plug-ins → `BitStream` → decode → read the mesh. |
| `adf.py` | Minimal ADF walker that finds each tooth's `CompressedData`. |
| `build_meshes.py` | CLI; writes the `.meshes.bin` sidecar (format in its docstring). |

**Container.** The file is `"mts` + big-endian version + `$$` + 1 byte, then a varint-sized header, then chunks:
`varint len · varint typeId · [typedef on first use: varint len, flag, strlen, name] · payload`.
The varint's top 2 bits give the number of extra bytes.
A type's stream is the concatenation of its chunks' payloads, and its first byte is the object index.
A tooth blob is 77 chunks: one opens `mesh`, one is a `dir` entry (`mesh` : class `Qedge`), and 75 are ~510-byte continuations.
Feeding the raw bytes instead of the concatenated payloads is what breaks a decoder after the base mesh.

**Mesh stream.** It's an LSB-first bitstream for `rtg2`'s progressive "UMF" mesh decoder (vtable at `0x11827cfc`).
It carries 32 flag bits, a `ReadUInt` version (it must be 0), an optional key if `flags & 1`, and per-attribute plug-in headers.
Then come six counts, a range-coded base mesh (`0x1181aef0`), and vertex-split records (`0x1181c5e0`) until the target count.
`ReadUInt` is a 5-bit length `L` followed by `L-1` bits, giving `(1 << (L-1)) | bits`.
Strings are a `ReadUInt` length followed by 8-bit chars. That is where the 47-bit `Fbits0…7` entries in the plug-in headers come from.

**Decoded mesh object.** `+0x6c` is the face count; faces are a linked list at `+0x70`.
Each face record holds vertex-record pointers at `+0x10/14/18` and the next face at `+0x20`.
Positions are `float32[3]` at `+0x104`, indexed by vertex-record index, with the count at `+0x108`.

## Validation on PM.adf

- All 54 blobs (27 crowns + 27 `InitialToothShape`s) decode to within the last byte of their stream.
- Every mesh is closed and genus 0 (`V − E + F = 2`), with median edge lengths of 0.21–0.33 mm.
- Decoded bounding boxes equal the stream-header bboxes to within 1 nm.
- The 354 plain-text interproximal vertices (`InitialIPPositions.newvtxData`) of tooth 2 each have a decoded vertex within 0.12 mm.
  Align's `newvtxIndices` ids are a different numbering of the same vertices.

## Next: a browser decoder

The emulator is the reference for a pure JavaScript port, which is what the viewer needs to open arbitrary ADFs.
Port one routine at a time (bit reader → range decoder and models → base mesh → vertex splits → plug-ins).
Check each routine by comparing its output against the emulator's memory at the same point.
