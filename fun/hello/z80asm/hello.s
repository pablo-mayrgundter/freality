        LD DE, HELLO
        LD B, 15
LOOP:   LD A,(DE)
        INC DE
        RST 10H
        DJNZ LOOP
        RET
HELLO   DEFM /Hello, World!\n/
