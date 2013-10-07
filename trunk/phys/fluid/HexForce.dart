part of phys;

/**
 * Force field on a hexagonal grid.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class HexForce extends Force {

  // Basic hex forces.
  static final int DR = 1, UR = 2, DL = 4, UL = 8, L = 16, R = 32;

  // Input/output rule pairs.
  static final int R1_I = DR | UL,              R1_O = L | R;
  static final int R2_I = L | DR | UL,          R2_O = L | UR | DL;
  static final int R3_I = L | UR | DL,          R3_O = L | UL | DR;
  static final int R4_I = L | DR | UR,          R4_O = R | DL | UL;
  static final int R5_I = DR | DL | UR | UL,    R5_O = L | R | UL | DR;

  // Only other non-symmetric rotation of R1.
  static final int R6_I = UR | DL,              R6_O = L | R;

  // Mirrors of 2 & 3
  static final int R7_I = R | UR | DL,          R7_O = R | UL | DR;
  static final int R8_I = R | DR | UL,          R8_O = R | UR | DL;

  // 4 rotations of 5-inputs.
  static final int R9_I = R | L | DR | UR | UL, R9_O = L | R | UL | DL | UR;
  static final int RA_I = R | L | DR | UR | DL, RA_O = L | R | UL | DL | DR;
  static final int RB_I = R | L | DL | UL | UR, RB_O = L | R | UR | DR | UL;
  static final int RC_I = R | L | DL | UL | DR, RC_O = L | R | UR | DR | DL;

  /*
  static {
    // Quick check that all rules are <= 64 bit.
    if ((R1_I | R1_O |
         R2_I | R2_O |
         R3_I | R3_O |
         R4_I | R4_O |
         R5_I | R5_O |
         R6_I | R6_O |
         R7_I | R7_O |
         R8_I | R8_O |
         R9_I | R9_O |
         RA_I | RA_O |
         RB_I | RB_O |
         RC_I | RC_O
         ) > 63) {
      throw new IllegalStateException("Dangling bits above force codes.");
    }
  }
  */

  void debug(msg) {
    StringBuffer s = new StringBuffer();
    s.add(msg);
    query('#info').text = s.toString();
  }

  void applyWithin(final int radius, final Space2D before, final Space2D after,
                   final int x, final int y) {
    debug(before.space.length);
    int f =
      before.get2(x - 1, y - 1) & DR
      | before.get2(x + 1, y - 1) & DL
      | before.get2(x - 1, y) & R
      | before.get2(x + 1, y) & L
      | before.get2(x - 1, y + 1) & UR
      | before.get2(x + 1, y + 1) & UL;

    int out = 0;
    switch (f) {
    case R1_I: out = R1_O; break;
    case R2_I: out = R2_O; break;
    case R3_I: out = R3_O; break;
    case R4_I: out = R4_O; break;
    case R5_I: out = R5_O; break;
    case R6_I: out = R6_O; break;
    case R7_I: out = R7_O; break;
    case R8_I: out = R8_O; break;
    case R9_I: out = R9_O; break;
    case RA_I: out = RA_O; break;
    case RB_I: out = RB_O; break;
    case RC_I: out = RC_O; break;
    default: out = f;
    }
    after.set(out, x, y);
  }
}
