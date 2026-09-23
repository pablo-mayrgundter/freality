"""Run Viewpoint's Mts3Reader.dll (the MetaStream 3 stream reader) under Unicorn.

Only the DLL's 30 C-runtime / Win32 imports are stubbed. Everything that
decodes geometry is the original x86 code, so its output is the reference
answer for an eventual JavaScript port.

The DLL is not in this repo. See README.md for where it comes from.
"""
import struct

import pefile
from keystone import Ks, KS_ARCH_X86, KS_MODE_32
from unicorn import Uc, UcError, UC_ARCH_X86, UC_MODE_32, UC_HOOK_CODE
from unicorn.x86_const import (
    UC_X86_REG_DS, UC_X86_REG_EAX, UC_X86_REG_EBP, UC_X86_REG_ECX, UC_X86_REG_EIP,
    UC_X86_REG_ES, UC_X86_REG_ESP, UC_X86_REG_FS, UC_X86_REG_GDTR, UC_X86_REG_SS,
)

STUB = 0x30000000
RET_MAGIC = STUB + 0xfff0
HEAP = 0x40000000
HEAP_SIZE = 0x10000000
STACK = 0x70000000
STACK_SIZE = 0x200000
TEB = 0x7ff00000
GDT = 0x7ff10000

# Import stubs written in x86 because they touch the FPU or the SEH chain.
ASM_STUBS = {
    '_EH_prolog': '''
        push -1
        push eax
        mov eax, dword ptr fs:[0]
        push eax
        mov eax, dword ptr [esp+0xc]
        mov dword ptr fs:[0], esp
        mov dword ptr [esp+0xc], ebp
        lea ebp, [esp+0xc]
        push eax
        ret''',
    '_initterm': '''
        push esi
        push edi
        mov esi, dword ptr [esp+0xc]
        mov edi, dword ptr [esp+0x10]
      next:
        cmp esi, edi
        jae done
        mov eax, dword ptr [esi]
        test eax, eax
        jz skip
        call eax
      skip:
        add esi, 4
        jmp next
      done:
        pop edi
        pop esi
        ret''',
    '_ftol': '''
        push ebp
        mov ebp, esp
        sub esp, 12
        fnstcw word ptr [ebp-2]
        mov ax, word ptr [ebp-2]
        or ah, 0xc
        mov word ptr [ebp-4], ax
        fldcw word ptr [ebp-4]
        fistp qword ptr [ebp-12]
        fldcw word ptr [ebp-2]
        mov eax, dword ptr [ebp-12]
        mov edx, dword ptr [ebp-8]
        leave
        ret''',
    'sqrt': '''
        fld qword ptr [esp+4]
        fsqrt
        ret''',
    'asin': '''
        fld qword ptr [esp+4]
        fld st(0)
        fmul st(0), st(0)
        fld1
        fsubrp st(1), st(0)
        fsqrt
        fpatan
        ret''',
    '_CIasin': '''
        fld st(0)
        fmul st(0), st(0)
        fld1
        fsubrp st(1), st(0)
        fsqrt
        fpatan
        ret''',
}

# stdcall imports: bytes of arguments popped by `ret n`.
STDCALL = {
    'QueryPerformanceFrequency': 4, 'DeleteCriticalSection': 4,
    'InitializeCriticalSection': 4, 'DisableThreadLibraryCalls': 4,
}


class Stop(Exception):
    pass


class Emu:
    def __init__(self, dll_path):
        pe = pefile.PE(dll_path)
        self.pe = pe
        self.base = pe.OPTIONAL_HEADER.ImageBase
        image = pe.get_memory_mapped_image()
        uc = Uc(UC_ARCH_X86, UC_MODE_32)
        self.uc = uc
        uc.mem_map(self.base, (len(image) + 0xfff) & ~0xfff)
        uc.mem_write(self.base, image)
        for addr, size in ((STUB, 0x10000), (HEAP, HEAP_SIZE), (STACK, STACK_SIZE),
                           (TEB, 0x10000), (GDT, 0x1000)):
            uc.mem_map(addr, size)
        self.heap_top = HEAP + 0x1000
        self.sizes = {}
        self.stop_reason = None
        self._setup_segments()
        self._setup_imports()

    # -- memory helpers ----------------------------------------------------
    def u32(self, addr):
        return struct.unpack('<I', self.uc.mem_read(addr, 4))[0]

    def w32(self, addr, value):
        self.uc.mem_write(addr, struct.pack('<I', value & 0xffffffff))

    def read(self, addr, n):
        return bytes(self.uc.mem_read(addr, n))

    def cstr(self, addr, limit=4096):
        return self.read(addr, limit).split(b'\0')[0].decode('latin1')

    def malloc(self, n):
        n = max(n, 1)
        p = self.heap_top
        self.heap_top = (self.heap_top + n + 15) & ~15
        if self.heap_top > HEAP + HEAP_SIZE:
            raise Stop('emulator heap exhausted')
        self.uc.mem_write(p, b'\0' * n)
        self.sizes[p] = n
        return p

    # -- CPU setup ---------------------------------------------------------
    def _setup_segments(self):
        """Flat 32-bit segments, plus FS pointing at a fake TEB for SEH."""
        uc = self.uc
        uc.mem_write(TEB, struct.pack('<I', 0xffffffff))
        uc.mem_write(TEB + 0x18, struct.pack('<I', TEB))

        def descriptor(base, limit, access, flags):
            d = limit & 0xffff
            d |= (base & 0xffffff) << 16
            d |= (access & 0xff) << 40
            d |= ((limit >> 16) & 0xf) << 48
            d |= (flags & 0xf) << 52
            d |= ((base >> 24) & 0xff) << 56
            return struct.pack('<Q', d)

        gdt = b''.join([
            descriptor(0, 0, 0, 0),
            descriptor(0, 0xfffff, 0x9a, 0xc),   # code
            descriptor(0, 0xfffff, 0x92, 0xc),   # data
            descriptor(TEB, 0xfff, 0x92, 0x4),   # fs
        ])
        uc.mem_write(GDT, gdt)
        uc.reg_write(UC_X86_REG_GDTR, (0, GDT, len(gdt) - 1, 0))
        for reg in (UC_X86_REG_SS, UC_X86_REG_DS, UC_X86_REG_ES):
            uc.reg_write(reg, 2 << 3)
        uc.reg_write(UC_X86_REG_FS, 3 << 3)

    def _setup_imports(self):
        ks = Ks(KS_ARCH_X86, KS_MODE_32)
        uc = self.uc
        self.py_stubs = {}
        addr = STUB + 0x100
        zero_dword = STUB + 0x10
        for dll in self.pe.DIRECTORY_ENTRY_IMPORT:
            for imp in dll.imports:
                name = imp.name.decode()
                if name == '_adjust_fdiv':  # a data import, not a function
                    self.w32(imp.address, zero_dword)
                    continue
                if name in ASM_STUBS:
                    code, _ = ks.asm(ASM_STUBS[name], addr)
                    uc.mem_write(addr, bytes(code))
                else:
                    pop = STDCALL.get(name, 0)
                    uc.mem_write(addr, b'\xc2' + struct.pack('<H', pop) if pop else b'\xc3')
                    self.py_stubs[addr] = name
                self.w32(imp.address, addr)
                addr += 0x80
        uc.mem_write(RET_MAGIC, b'\xf4')
        uc.hook_add(UC_HOOK_CODE, self._on_stub, begin=STUB, end=STUB + 0xffff)

    def _arg(self, i):
        esp = self.uc.reg_read(UC_X86_REG_ESP)
        return self.u32(esp + 4 + 4 * i)

    def _on_stub(self, uc, address, size, _):
        name = self.py_stubs.get(address)
        if name is None:
            return
        a = self._arg
        ret = 0
        if name in ('malloc', '??2@YAPAXI@Z'):
            ret = self.malloc(a(0))
        elif name in ('free', '??1type_info@@UAE@XZ', 'DeleteCriticalSection',
                      'InitializeCriticalSection', '?_set_new_mode@@YAHH@Z',
                      '?_set_new_handler@@YAP6AHI@ZP6AHI@Z@Z'):
            ret = 0
        elif name == 'realloc':
            p, n = a(0), a(1)
            ret = self.malloc(n)
            if p:
                uc.mem_write(ret, self.read(p, min(self.sizes.get(p, n), n)))
        elif name in ('memcpy', 'memmove'):
            d, s, n = a(0), a(1), a(2)
            if n:
                uc.mem_write(d, self.read(s, n))
            ret = d
        elif name == 'memset':
            d, c, n = a(0), a(1), a(2)
            if n:
                uc.mem_write(d, bytes([c & 0xff]) * n)
            ret = d
        elif name == 'strlen':
            ret = len(self.cstr(a(0)))
        elif name == 'strcmp':
            x, y = self.cstr(a(0)), self.cstr(a(1))
            ret = (x > y) - (x < y)
        elif name == '_strdup':
            s = self.cstr(a(0)).encode('latin1') + b'\0'
            ret = self.malloc(len(s))
            uc.mem_write(ret, s)
        elif name == 'labs':
            ret = abs(struct.unpack('<i', struct.pack('<I', a(0)))[0])
        elif name in ('_onexit', '__dllonexit'):
            ret = a(0)
        elif name == 'QueryPerformanceFrequency':
            self.w32(a(0), 1000000)
            self.w32(a(0) + 4, 0)
            ret = 1
        elif name == 'DisableThreadLibraryCalls':
            ret = 1
        elif name == '_CxxThrowException':
            raise Stop('C++ exception: %s' % self._exception_text(a(0)))
        else:
            raise Stop('unexpected call to %s' % name)
        uc.reg_write(UC_X86_REG_EAX, ret & 0xffffffff)

    def _exception_text(self, obj):
        texts = []
        for i in range(4):
            try:
                s = self.cstr(self.u32(obj + 4 * i), 80)
            except UcError:
                continue
            if len(s) >= 3 and s.isprintable():
                texts.append(s)
        return texts

    # -- calls -------------------------------------------------------------
    def call(self, fn, *args, this=None):
        """cdecl / stdcall / thiscall (`this` goes in ECX). Returns EAX."""
        uc = self.uc
        esp = STACK + STACK_SIZE - 0x1000
        for arg in reversed(args):
            esp -= 4
            self.w32(esp, arg)
        esp -= 4
        self.w32(esp, RET_MAGIC)
        uc.reg_write(UC_X86_REG_ESP, esp)
        uc.reg_write(UC_X86_REG_EBP, 0)
        if this is not None:
            uc.reg_write(UC_X86_REG_ECX, this)
        self.stop_reason = None
        try:
            uc.emu_start(fn, RET_MAGIC)
        except Stop as e:
            self.stop_reason = str(e)
            uc.emu_stop()
        except UcError as e:
            self.stop_reason = '%s at eip %#x' % (e, uc.reg_read(UC_X86_REG_EIP))
        return uc.reg_read(UC_X86_REG_EAX)

    def dll_main(self):
        entry = self.base + self.pe.OPTIONAL_HEADER.AddressOfEntryPoint
        self.call(entry, self.base, 1, 0)  # DLL_PROCESS_ATTACH: runs static ctors
        if self.stop_reason:
            raise RuntimeError('DllMain failed: ' + self.stop_reason)
