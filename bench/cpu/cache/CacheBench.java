package benchmarking.cpu.cache;

import java.util.Random;

class CacheBench {

    public static void main(String [] args) {

        final int cacheSizeInKB = Integer.parseInt(args[0]); // e.g. 512 for the CPU described above.
        final int maxBufSizeBytes = cacheSizeInKB * 4 * 1024; // * 4 is the max size tested
        final double numIts = 1000;

        System.out.println("#bufSize, bytes/ms");

        int x = 0;
        long time;
        Random r = new Random();
        for (int times = 0; times < 2; times++) {// First time for warmup, second for report.
            for (int bufSize = 4; bufSize <= maxBufSizeBytes; bufSize*=2) {
                final byte [] buf = new byte[bufSize];
                time = System.currentTimeMillis();
                for (int i = 0; i < numIts; i++)
                    for (int j = 0; j < buf.length; j++)
                        x += (int)buf[j]; // buf[r.nextInt(bufSize)] <- use this instead for random-access test.
                time = System.currentTimeMillis() - time;
                System.out.println(bufSize + ", " + ((numIts*(double)buf.length/(double)time)));
            }
        }
    }
}
