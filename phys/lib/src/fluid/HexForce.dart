import 'dart:html';
import 'package:phys/src/Force.dart';
import 'package:phys/src/Space2D.dart';

/**
 * Force field on a hexagonal grid.
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class HexForce extends Force {

  // Basic hex forces.
  static const int DR = 1, UR = 2, DL = 4, UL = 8, L = 16, R = 32;

  // Input/output rule pairs.  Each rule, e.g. R1, is directly from Wolfram,
  // though it seems he omits symmetrical rotations from the text.  Rotations
  // have an additional letter.  Input appended with _I, output with _O.
  // Vectors are OR'd together in clockwise order, starting with the force
  // coming from in the case of input, or going out in the case of output, the
  // top-right of the hexagon.
  static const int R1A_I = UL | DR,             R1A_O = R | L;
  static const int R1B_I = DL | UR,             R1B_O = DR | UL;
  static const int R1C_I = L | R,               R1C_O = UR | DL;

  static const int R2A_I = L | UL | DR,          R2A_O = UR | DL | L;
  static const int R2B_I = DL | UL | UR,         R2B_O = R | L | UL;
  static const int R2C_I = L | UR | R,           R2C_O = UR | DR | UL;
  static const int R2D_I = UL | R | DR,          R2D_O = UR | R | DL;
  static const int R2E_I = DL | UR | DR,         R2E_O = R | DR | L;
  static const int R2F_I = DL | L | R,           R2F_O = DR | DL | UL;

  static const int R3A_I = DL | L | UR,          R3A_O = DR | L | UR;
  static const int R3B_I = L | UL | R,           R3B_O = UR | DL | UL;
  static const int R3C_I = UL | UR | DR,         R3C_O = UR | R | L;
  static const int R3D_I = DL | UR | R,          R3D_O = R | DR | UL;
  static const int R3E_I = L | R | DR,           R3E_O = UR | DR | DL;
  static const int R3F_I = DL | UL | DR,         R3F_O = R | DL | L;

  static const int R4A_I = L | UR | DR,          R4A_O = R | DL | UL;
  static const int R4B_I = DL | UL | R,          R4B_O = UR | DR | L;

  static const int R5A_I = DL | UL | UR | DR,    R5A_O = R | DR | L | UL;
  static const int R5B_I = DL | L | UR | R,      R5B_O = UR | DR | DL | UL;
  static const int R5C_I = L | UL | R | DR,      R5C_O = UR | R | DL | L;

  // DR missing                                   UL missing
  static const int R6A_I = DL | L | UL | UR | R,  R6A_O = DL | L | UR | R | DR;
  // R                                            L
  static const int R6B_I = DL | L | UL | UR | DR, R6B_O = DL | UL | UR | R | DR;
  // UR                                           DL
  static const int R6C_I = DL | L | UL | R | DR,  R6C_O = L | UL | UR | R | DR;
  // UL                                           DR
  static const int R6D_I = DL | L | UR | R | DR,  R6D_O = DL | L | UL | UR | R;
  // L                                            R
  static const int R6E_I = DL | L | UL | UR | R,  R6E_O = DL | L | UL | UR | DR;
  // DL                                           UR
  static const int R6F_I = L | UL | UR | R | DR,  R6F_O = DL | L | UL | R | DR;

  // TODO(pablo): move to a unit test.
  HexForce() {
    // Quick check that all rules are <= 64 bit.
    if ((R1A_I | R1A_O |
         R1B_I | R1B_O |
         R1C_I | R1C_O |
         R2A_I | R2A_O |
         R2B_I | R2B_O |
         R2C_I | R2C_O |
         R2D_I | R2D_O |
         R2E_I | R2E_O |
         R2F_I | R2F_O |
         R3A_I | R3A_O |
         R3B_I | R3B_O |
         R3C_I | R3C_O |
         R3D_I | R3D_O |
         R3E_I | R3E_O |
         R3F_I | R3F_O |
         R4A_I | R4A_O |
         R4B_I | R4B_O |
         R5A_I | R5A_O |
         R5B_I | R5B_O |
         R5C_I | R5C_O |
         R6A_I | R6A_O |
         R6B_I | R6B_O |
         R6C_I | R6C_O |
         R6D_I | R6D_O |
         R6E_I | R6E_O |
         R6F_I | R6F_O) > 63) {
      throw new StateError("Dangling bits above force codes.");
    }
  }

  void applyWithin(final int radius, final Space2D before, final Space2D after,
                   final int x, final int y) {
    int f =
      before.get2(x - 1, y - 1) & DR
      | before.get2(x + 1, y - 1) & DL
      | before.get2(x - 1, y) & R
      | before.get2(x + 1, y) & L
      | before.get2(x - 1, y + 1) & UR
      | before.get2(x + 1, y + 1) & UL;

    int out = 0;
    switch (f) {
      case R1A_I: out = R1A_O; break;
      case R1B_I: out = R1B_O; break;
      case R1C_I: out = R1C_O; break;
      case R2A_I: out = R2A_O; break;
      case R2B_I: out = R2B_O; break;
      case R2C_I: out = R2C_O; break;
      case R2D_I: out = R2D_O; break;
      case R2E_I: out = R2E_O; break;
      case R2F_I: out = R2F_O; break;
      case R3A_I: out = R3A_O; break;
      case R3B_I: out = R3B_O; break;
      case R3C_I: out = R3C_O; break;
      case R3D_I: out = R3D_O; break;
      case R3E_I: out = R3E_O; break;
      case R3F_I: out = R3F_O; break;
      case R4A_I: out = R4A_O; break;
      case R4B_I: out = R4B_O; break;
      case R5A_I: out = R5A_O; break;
      case R5B_I: out = R5B_O; break;
      case R5C_I: out = R5C_O; break;
      case R6A_I: out = R6A_O; break;
      case R6B_I: out = R6B_O; break;
      case R6C_I: out = R6C_O; break;
      case R6D_I: out = R6D_O; break;
      case R6E_I: out = R6E_O; break;
      case R6F_I: out = R6F_O; break;
      default: out = f;
    }
    after.set2(out, x, y);
  }
}
