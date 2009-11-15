	.ORG $0200
	LDX #0
l:	LDA d, X
	STA $E001
	INX
	BNE l
	RTS
d:	.DB "Hello World!"
	.DB $0
