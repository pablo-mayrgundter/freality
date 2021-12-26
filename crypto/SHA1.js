class ByteBuffer {
  constructor(sizeOrBuf) {
    if (typeof sizeOrBuf === 'number') {
      this.buf = new Int8Array(sizeOrBuf);
    } else if (sizeOrBuf instanceof Int8Array) {
      this.buf = sizeOrBuf;
    } else {
      throw new Error('Illegal argument: ' + sizeOrBuf);
    }
    this.positionNdx = 0;
    this.limitNdx = this.buf.length;
  }
  put(b) {
    if (this.positionNdx >= this.limitNdx) {
      throw new Error(`Buffer full: position(${this.positionNdx}) limit(${this.limitNdx})`);
    }
    this.buf[this.positionNdx++] = b;
  }
  putLong(l) {
    // TODO
    this.put(l);
  }
  flip() {
    // TODO
  }
  getInt(ndx) {
    this.buf[ndx];
  }
  position(ndx) {
    if (ndx) {
      this.positionNdx = ndx;
      return this;
    }
    return this.positionNdx;
  }
  limit(ndx) {
    if (ndx) {
      this.limitNdx = ndx;
      return this;
    }
    return this.limitNdx;
  }
  slice() {
    let s = new ByteBuffer(this.buf.slice(this.positionNdx, this.limitNdx))
    console.log('s: ', s);
    return s;
  }
}

const Integer = {
  rotateLeft: (x) => {
    let y = x;
    return y;
  },
  toHexString: (x) => {
    console.log('toHexString on : ', x);
    return x.toString(16);
  }
}

function checkNum(...args) {
  for (x in args) {
    const arg = args[x]
    if (isNaN(arg)) {
      throw new Error('Is not a number: ' + arg);
    }
  }
  console.log('checkNum: ', args);
}


/**
 * Based on pseudocode from Wikipedia and
 * http://www.itl.nist.gov/fipspubs/fip180-1.htm
 *
 * SHA1.java time to digest 930M file:
 * real    0m39.278s
 * user    0m39.152s
 * sys     0m0.043s
 *
 * sha1sum:
 * real    0m41.311s
 * user    0m26.689s
 * sys     0m1.266s
 *
 * @author pablo@freality.com
 * @version $Revision: 1.5 $
 */
function SHA1(input) {

  // Initialize variables:
  let h0 = 0x67452301;
  let h1 = 0xEFCDAB89;
  let h2 = 0x98BADCFE;
  let h3 = 0x10325476;
  let h4 = 0xC3D2E1F0;

  const w = new Int32Array(80);
  let a, b, c, d, e;
  let f, k, temp;


  /** Currently only works for messages length < 2^32 bits. */
  function addDigest(/* ByteBuffer */ src) {

    // Process the message in successive 512-bit (64 byte) chunks:
    // break message into 512-bit (64 byte) chunks
    // Break chunk into sixteen 32-bit big-endian words w[i], 0 <= i <= 15.
    let i = 0;
    for (i = 0; i < 16; i++) {
      w[i] = src.getInt(i*4);
    }

    for (i = 16; i < 80; i++) {
      w[i] = 0;
    }

    // Compute/add 1 digest line.
    // Extend the sixteen 32-bit (4 byte) words into eighty 32-bit (4 byte) words:
    for (i = 16; i < 80; i++) {
      w[i] = Integer.rotateLeft(w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16], 1);
    }

    // Initialize hash value for this chunk:
    f = k = temp = 0;
    a = h0;
    b = h1;
    c = h2;
    d = h3;
    e = h4;

    for (i = 0; i < 20; i++) {
      f = (b & c) | ((~b) & d);
      k = 0x5A827999;
      finishValues(i);
    }

    for (i = 20; i < 40; i++) {
      f = b ^ c ^ d;
      k = 0x6ED9EBA1;
      finishValues(i);
    }

    for (i = 40; i < 60; i++) {
      f = (b & c) | (b & d) | (c & d);
      k = 0x8F1BBCDC;
      finishValues(i);
    }

    for (i = 60; i < 80; i++) {
      f = b ^ c ^ d;
      k = 0xCA62C1D6;
      finishValues(i);
    }

    // Add this chunk's hash to result so far:
    h0 += a;
    h1 += b;
    h2 += c;
    h3 += d;
    h4 += e;
    checkNum(a, b, c, d, e);
  }

  function finishValues(/* int */ i) {
    console.log('finishValues: ', i, a, b, c, d, e, f, k)
    temp = Integer.rotateLeft(a, 5) + f + e + k + w[i];
    e = d;
    d = c;
    c = Integer.rotateLeft(b, 30);
    b = a;
    a = temp;
  }

  function pad(/* ByteBuffer */ src, /* long */ wholeMsgLength) {
    const /* ByteBuffer */ padded = new ByteBuffer(128);
    padded.put(src);
    padded.put(128);

    if (padded.position() < 56) {
      while (padded.position() < 56) {
        padded.put(0x0);
      }
    } else {
      while (padded.position() < 120) {
        padded.put(0x0);
      }
    }

    padded.putLong(wholeMsgLength);
    padded.flip();
    return padded;
  }

  const /* String */ ZEROS = "00000000";
  padStr = (s) => {
    console.log('padStr: ', s);
    if (s.length > 8) {
      return s.substring(0, 8); //(s.length - 8)
    }
    return ZEROS.substring(s.length) + s;
  }

  function toHexString(x) {
    if (isNaN(x)) {
      throw new Error('Is not a number: ', x);
    }
    return padStr(Integer.toHexString(x));
  }

  function getHash() {
    console.log(`Getting hash... h0(${h0}) h1(${h1}) h2(${h2}) h3(${h3}) h4(${h4})`);
    return toHexString(h0)
      + toHexString(h1)
      + toHexString(h2)
      + toHexString(h3)
      + toHexString(h4);
  }

  function process(/* ByteBuffer */ src) {
    if (src.limit() >= 64) {
      for (let i = 0, n = src.limit() / 64; i < n; i++) {
        const offset = i * 64;
        src.position(offset).limit(offset + 64);
        //addDigest(src);
        // break message into 512-bit (64 byte) chunks
        // Break chunk into sixteen 32-bit big-endian words w[i], 0 <= i <= 15.
        let j = 0;
        for (j = 0; j < 16; j++) {
          w[j] = src.getInt(j*4);
        }

        for (j = 16; j < 80; j++) {
          w[j] = 0;
        }

        // Compute/add 1 digest line.
        // Extend the sixteen 32-bit (4 byte) words into eighty 32-bit (4 byte) words:
        for (j = 16; j < 80; j++) {
          w[j] = Integer.rotateLeft(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
        }

        // Initialize hash value for this chunk:
        f = k = temp = 0;
        a = h0;
        b = h1;
        c = h2;
        d = h3;
        e = h4;

        for (j = 0; j < 20; j++) {
          f = (b & c) | ((~b) & d);
          k = 0x5A827999;
          finishValues(j);
        }

        for (j = 20; j < 40; j++) {
          f = b ^ c ^ d;
          k = 0x6ED9EBA1;
          finishValues(j);
        }

        for (j = 40; j < 60; j++) {
          f = (b & c) | (b & d) | (c & d);
          k = 0x8F1BBCDC;
          finishValues(j);
        }

        for (j = 60; j < 80; j++) {
          f = b ^ c ^ d;
          k = 0xCA62C1D6;
          finishValues(j);
        }

        // Add this chunk's hash to result so far:
        h0 += a;
        h1 += b;
        h2 += c;
        h3 += d;
        h4 += e;
      }
    }

    const /* ByteBuffer */ padded = pad(src, input.length * 8);
    addDigest(padded);
    if (padded.limit() == 128) {
      padded.position(64);
      addDigest(padded.slice());
    }
    return getHash();
  }

  return process(input);
}

const src = new ByteBuffer(10);
src.buf[0] = 0xdeadbeef + 34234234;
src.buf[1] = 0xdeadbeef + 1;
src.buf[2] = 0xdeadbeef + 1231231;
src.buf[3] = 0xdeadbeef;
console.log(SHA1(src));
