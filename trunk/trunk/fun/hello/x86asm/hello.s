.text
message:
        .ascii "Hello, World!\n"
        .align 4
        .globl E
E:      
        movb $4, %al
        movl $message, %ecx
        movb $15, %dl
        int $0x80
	xorl %ebx, %ebx
        movb $1, %al
        int $0x80
