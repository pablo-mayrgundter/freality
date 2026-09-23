"""MetaStream (.mts) container parsing, plus mesh decoding via the emulated DLL.

Container (reverse engineered from Mts3Reader.dll):

    '"mts'  uint32be version (<= 2)  '$$'  1 byte
    varint headerLen, headerLen bytes
    repeat: varint chunkLen, then chunkLen bytes:
        varint typeId
        if typeId is new: varint defLen, then defLen bytes: flag, strlen, name
        payload

varint: the top 2 bits of the first byte give the number of extra bytes,
big-endian; the low 6 bits are the most significant bits.

A type's stream is the concatenation of its chunks' payloads. The first
payload byte after a type definition is the object index. For "mesh" the rest
is one LSB-first bitstream for rtg2's progressive UMF mesh decoder.
"""
import numpy as np

from emu import Emu

# Addresses in Mts3Reader.dll 3.0.15.12 (image base 0x11800000).
DECODER_FACTORY = 0x1180f6d0   # new UMF stream decoder, 0x1a4 bytes
BITSTREAM_CTOR = 0x118094e0    # rtg2::BitStream(int bytes), thiscall
DEFAULT_PLUGINS = 0x1180f280   # push the stock attribute plug-ins onto a list
VT_DECODE_FROM = 0x08          # decoder vtable: SetStream + decode everything
VT_TAKE_MESH = 0x28            # decoder vtable: hand over the decoded mesh
VT_ADD_PLUGINS = 0x3c          # decoder vtable: register attribute plug-ins
# Decoded mesh object layout.
MESH_FACE_COUNT = 0x6c
MESH_FACE_LIST = 0x70          # linked list; face: +0x10/14/18 vertex*, +0x20 next
MESH_POSITIONS = 0x104         # float32[3] array indexed by vertex record index
MESH_VERTEX_COUNT = 0x108


def varint(buf, pos):
    b = buf[pos]
    n = b >> 6
    v = b & 63
    for i in range(n):
        v = (v << 8) | buf[pos + 1 + i]
    return v, pos + 1 + n


def streams(buf, start=0):
    """Return {typeId: (typeName, objectIndex, payloadBytes)} for one .mts stream."""
    if buf[start:start + 4] != b'"mts' or buf[start + 8:start + 10] != b'$$':
        raise ValueError('not a MetaStream stream')
    pos = start + 11
    header_len, pos = varint(buf, pos)
    pos += header_len
    out = {}
    while pos < len(buf):
        length, body = varint(buf, pos)
        end = body + length
        type_id, p = varint(buf, body)
        if type_id not in out:
            def_len, p = varint(buf, p)
            definition = buf[p:p + def_len]
            p += def_len
            name = definition[2:2 + definition[1]].decode('latin1')
            out[type_id] = [name, buf[p], bytearray(buf[p + 1:end])]
        else:
            out[type_id][2] += buf[p:end]
        pos = end
    return {k: (v[0], v[1], bytes(v[2])) for k, v in out.items()}


class Decoder:
    def __init__(self, dll_path):
        self.dll_path = dll_path

    def decode(self, payload):
        """Decode one mesh bitstream. Returns (positions float32 Nx3, faces int32 Mx3)."""
        e = Emu(self.dll_path)
        e.dll_main()
        dec = e.call(DECODER_FACTORY)
        vt = e.u32(dec)
        plugins = e.malloc(12)
        e.call(DEFAULT_PLUGINS, plugins)
        e.call(e.u32(vt + VT_ADD_PLUGINS), plugins, this=dec)
        bs = e.malloc(0x40)
        e.call(BITSTREAM_CTOR, len(payload) + 64, this=bs)
        data = e.malloc(len(payload) + 64)
        e.uc.mem_write(data, payload)
        # BitStream: vt, data, bitPos, capacityBits, limitBits, own, mode, base, max
        e.w32(bs + 0x04, data)
        e.w32(bs + 0x08, 0)
        e.w32(bs + 0x0c, (len(payload) + 64) * 8)
        e.w32(bs + 0x10, len(payload) * 8)
        e.w32(bs + 0x18, 0xa)  # read mode
        e.w32(bs + 0x1c, 0)
        e.w32(bs + 0x20, 0x7fffffff)
        e.call(e.u32(vt + VT_DECODE_FROM), bs, this=dec)
        if e.stop_reason:
            raise RuntimeError('decoder stopped: ' + e.stop_reason)
        bits_used = e.u32(bs + 0x08)
        if len(payload) * 8 - bits_used >= 8:
            raise RuntimeError('decoder stopped early at bit %d of %d' % (bits_used, len(payload) * 8))
        mesh = e.call(e.u32(vt + VT_TAKE_MESH), this=dec)
        if e.stop_reason or not mesh:
            raise RuntimeError('no mesh: %s' % e.stop_reason)
        nv = e.u32(mesh + MESH_VERTEX_COUNT)
        positions = np.frombuffer(e.read(e.u32(mesh + MESH_POSITIONS), 12 * nv), np.float32).reshape(-1, 3).copy()
        nf = e.u32(mesh + MESH_FACE_COUNT)
        faces = np.empty((nf, 3), np.int32)
        face = e.u32(mesh + MESH_FACE_LIST)
        for i in range(nf):
            faces[i] = [e.u32(e.u32(face + o)) for o in (0x10, 0x14, 0x18)]
            face = e.u32(face + 0x20)
        return positions, faces

    def decode_blob(self, blob):
        """Decode an ADF CompressedData value (uint32 size prefix + .mts stream)."""
        meshes = [s for s in streams(blob, 4).values() if s[0] == 'mesh']
        if len(meshes) != 1:
            raise ValueError('expected one mesh stream, got %d' % len(meshes))
        return self.decode(meshes[0][2])
