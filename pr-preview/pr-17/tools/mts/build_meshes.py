"""Decode every tooth surface in an ADF and write a compact mesh sidecar.

    python build_meshes.py PM.adf path/to/Mts3Reader.dll            # -> PM.meshes.bin
    python build_meshes.py PM.adf Mts3Reader.dll --initial --obj out/

Sidecar format (little-endian), read by src/mesh-sidecar.js:

    char[4] 'ADFM'  uint32 version=1  uint32 meshCount
    per mesh, 4-byte aligned:
      int32 toothId  uint32 kind (0 crown = Tooth.CompressedQedge,
                                  1 initial = InitialToothShape)
      uint32 vertexCount  uint32 faceCount
      float32 min[3]  float32 max[3]            (metres, jaw space)
      uint16 position[3 * vertexCount]          p = min + q / 65535 * (max - min)
      uint16|uint32 index[3 * faceCount]        uint16 when vertexCount <= 65535
      zero padding to a multiple of 4 bytes

Faces are wound counter-clockwise seen from outside (three.js front faces).
"""
import argparse
import os
import struct
import sys
import time

import numpy as np

import adf
from mts import Decoder

KIND_CROWN, KIND_INITIAL = 0, 1


def signed_volume(p, f):
    a, b, c = p[f[:, 0]].astype(np.float64), p[f[:, 1]].astype(np.float64), p[f[:, 2]].astype(np.float64)
    return np.einsum('ij,ij->i', a, np.cross(b, c)).sum() / 6


def pack(tooth_id, kind, p, f):
    lo, hi = p.min(0), p.max(0)
    span = np.where(hi > lo, hi - lo, 1)
    q = np.round((p - lo) / span * 65535).astype('<u2')
    idx = f.astype('<u2' if len(p) <= 65535 else '<u4')
    out = struct.pack('<iIII6f', tooth_id, kind, len(p), len(f), *lo, *hi) + q.tobytes() + idx.tobytes()
    return out + b'\0' * (-len(out) % 4)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument('adf')
    ap.add_argument('dll', help='Mts3Reader.dll from Viewpoint Media Player 3.0.15.12')
    ap.add_argument('-o', '--out', help='sidecar path (default: <adf without .adf>.meshes.bin)')
    ap.add_argument('--initial', action='store_true', help='also include InitialToothShape meshes')
    ap.add_argument('--obj', metavar='DIR', help='also write one OBJ per mesh')
    args = ap.parse_args()

    buf = open(args.adf, 'rb').read()
    decoder = Decoder(args.dll)
    records = []
    t0 = time.time()
    for jaw, tid, crown, initial in adf.teeth(adf.parse(buf)):
        for kind, blob in ((KIND_CROWN, crown), (KIND_INITIAL, initial)):
            if blob is None or (kind == KIND_INITIAL and not args.initial):
                continue
            p, f = decoder.decode_blob(blob)
            if signed_volume(p, f) < 0:
                f = f[:, ::-1].copy()
            records.append(pack(tid, kind, p, f))
            label = '%s tooth %2d %-7s' % (jaw, tid, 'crown' if kind == KIND_CROWN else 'initial')
            print('%s %5d verts %5d faces  %.1fs' % (label, len(p), len(f), time.time() - t0))
            if args.obj:
                os.makedirs(args.obj, exist_ok=True)
                name = 'tooth%02d_%s.obj' % (tid, 'crown' if kind == KIND_CROWN else 'initial')
                with open(os.path.join(args.obj, name), 'w') as fh:
                    fh.writelines('v %.7f %.7f %.7f\n' % tuple(v) for v in p)
                    fh.writelines('f %d %d %d\n' % tuple(t + 1) for t in f)
    out = args.out or (args.adf[:-4] if args.adf.lower().endswith('.adf') else args.adf) + '.meshes.bin'
    with open(out, 'wb') as fh:
        fh.write(b'ADFM' + struct.pack('<II', 1, len(records)))
        for r in records:
            fh.write(r)
    print('wrote %s (%d meshes, %d bytes)' % (out, len(records), os.path.getsize(out)))


if __name__ == '__main__':
    sys.exit(main())
