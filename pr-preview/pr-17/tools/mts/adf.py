"""Minimal ADF container walker: enough to find each tooth's CompressedData.

Mirrors the rules in src/adf-parser.js: `{ name <uint32le size> <payload> ... }`,
where a payload is nested objects (optionally after a zero prefix), an
unbraced field body, or an opaque value.
"""
import re
import struct

NAME = re.compile(rb'[A-Za-z0-9+._\-:/<>*?#]+ ')
HEADER = re.compile(rb'AlignDataFile \( bin \)\nVersion [^\n]+\n\n')


class Node:
    __slots__ = ('name', 'raw', 'children')

    def __init__(self, name, raw, children=None):
        self.name = name
        self.raw = raw
        self.children = children

    def get(self, name):
        return [c for c in self.children or [] if c.name == name]

    def first(self, *path):
        node = self
        for name in path:
            found = node.get(name)
            if not found:
                return None
            node = found[0]
        return node


def _fields(buf, pos, end):
    """Parse `name size payload` fields and `{...}` groups until end or '}'."""
    out = []
    while pos < end and buf[pos] != 0x7d:
        if buf[pos] == 0x7b:
            inner, pos = _object(buf, pos, end)
            out.extend(inner)
            continue
        m = NAME.match(buf, pos, end)
        if not m:
            raise ValueError('field name expected at %d' % pos)
        name = m.group()[:-1].decode('ascii')
        p = m.end()
        size = struct.unpack_from('<I', buf, p)[0]
        p += 4
        if p + size > end:
            raise ValueError('field %s overruns at %d' % (name, p))
        out.append(Node(name, buf[p:p + size], _value(buf, p, p + size)))
        pos = p + size
    return out, pos


def _object(buf, pos, end):
    if buf[pos] != 0x7b:
        raise ValueError("'{' expected at %d" % pos)
    fields, pos = _fields(buf, pos + 1, end)
    if pos >= end or buf[pos] != 0x7d:
        raise ValueError('unclosed object at %d' % pos)
    return fields, pos + 1


def _value(buf, start, end):
    n = end - start
    if n == 0:
        return None
    skip = 0
    if buf[start] != 0x7b:
        for k in range(4, min(16, n - 1) + 1, 4):
            if buf[start + k] == 0x7b and not any(buf[start:start + k]):
                skip = k
                break
    if buf[start + skip] == 0x7b and buf[end - 1] == 0x7d:
        try:
            out, pos = [], start + skip
            while pos < end:
                fields, pos = _object(buf, pos, end)
                out.extend(fields)
            return out
        except ValueError:
            pass
    if NAME.match(buf, start, end):
        try:
            fields, pos = _fields(buf, start, end)
            if pos == end:
                return fields
        except (ValueError, struct.error):
            pass
    return None


def parse(buf):
    m = HEADER.match(buf)
    if not m:
        raise ValueError('not an AlignDataFile (bin)')
    fields, _ = _object(buf, m.end(), len(buf))
    return Node('root', buf, fields)


def teeth(root):
    """Yield (jaw, toothId, crownBlob, initialShapeBlob) for every tooth."""
    pair = root.first('JawPair') or root
    for jaw in ('upper', 'lower'):
        j = pair.first(jaw)
        if not j:
            continue
        for tooth in j.get('Tooth'):
            tid = struct.unpack('<i', tooth.first('id').raw)[0]
            crown = tooth.first('CompressedQedge', 'CompressedData')
            initial = tooth.first('QedgeToothDesigner', 'InitialToothShape', 'CompressedQedge', 'CompressedData')
            yield jaw, tid, crown.raw if crown else None, initial.raw if initial else None
