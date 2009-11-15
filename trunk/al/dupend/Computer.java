package al.dupend;

/**
 * The Computer class is an attempt to design a simple computer to
 * serve as a test framework for artificial life experiments.  The
 * instruction set language is tailored mainly for efficient
 * duplication of arbitrary sequences of instructions.  Therefore, the
 * simplest self-duplicating program is "dup; end;", and hence the
 * name of the language, dupend.
 *
 * One of the first design points with dupend is how to handle
 * self-reference and duplication-reference.  A simple program like:
 *
 *   0: dup
 *   1: end
 *
 * is intended to be capable of duplicating itself to "some other
 * location", to be determined by the runtime.  To do this, the dup
 * instruction must be taken to mean "start copying here (iclusive)"
 * and so too end "stop copying here (inclusive)".
 *
 * This is similar to the UNIX fork() operation, in that once
 * completed, two copies of the same program exist, with the only
 * difference being that they both proceed in separate execution
 * paths.
 */
final class Computer {

    static enum Op {
        DUP,
        END,
        NOOPA,
        NOOPB,
        NOOPC
    }

    static final class I {

        Op op = null;
        int arg = 0;
        int arg2 = 0;

        I setOp(Op op) { this.op = op; return this; }
        I setArg(int x) { this.arg = x; return this; }
        I setArg2(int x) { this.arg2 = x; return this; }

        public String toString() { return op + " " + arg + " " + arg2; }
    }

    final I [] mem;

    Computer (final int size) {
        mem = new I[size];
        for (int i = 0; i < size; i++)
            mem[i] = new I();
    }

    void run () {

        I i = mem[0];

        while (true) {

            //            switch (i.op) {
                //            case DUP: i = mem[i.arg]; break;
                //            case SET: reg[i.arg] = i.arg2; break;
            //            }

        }
    }

    public static void main(final String [] args) {
        final Computer c = new Computer(10);
        /*
        c.mem[0].setOp(Op.JMP).setArg(5);
        c.mem[3].setOp(Op.SET).setArg(0);
        c.mem[5].setOp(Op.JMP).setArg(3);
        */
        c.run();
    }
}
