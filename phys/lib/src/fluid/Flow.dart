import 'package:phys/src/Force.dart';
import 'package:phys/src/Space2D.dart';
import 'package:phys/src/fluid/HexForce.dart';
import 'package:phys/src/fluid/FlowGraph.dart';

/**
 * Wolfram models a fluid as particle interactions on a hexagonal
 * field (p. 378):
 *
 * \ /
 * -x-
 * / \
 *
 * Each cell has incident forces to it at the beginning of each step.
 * The evolution rule specifies how the cell transfers these forces
 * outwards by the end of the step.  Wolfram seems to pick a
 * particular rule which transfers the forces most symmetrically for
 * this configuration, and assumes that the count of incident fields
 * is half, or 3/6.
 *
 * Solids within the flow are represented with an additional ...
 *
 * @author Pablo Mayrgundter <pablo.mayrgundter@gmail.com>
 */
class Flow {

  int radius;
  FlowGraph flowGraph;
  Space2D space, next;
  Force force;
  double wallLeftR = 0.6;
  int wallLeft, wallLo, wallHi;
  final int forceRight =
      HexForce.R |
      HexForce.UR |
      HexForce.DR;
  final int forceLeft =
      HexForce.L |
      HexForce.UL |
      HexForce.DL;

  Flow(var canvas, int gridScale) {
    flowGraph = new FlowGraph(canvas, gridScale);
    flowGraph.prepDraw(this.coordPopCount);
    radius = flowGraph.radius;
    space = new Space2D(radius);
    next = new Space2D(radius);
    force = new HexForce();
    wallLo = (radius.toDouble() * 0.25).toInt();
    wallHi = (radius.toDouble() * 0.75).toInt();
    setWallLeftR(wallLeftR);
  }

  /** public */
  void setWallLeftR(double wallLeftR) {
    this.wallLeftR = wallLeftR;
    wallLeft = (radius.toDouble() * wallLeftR).toInt();
  }

  /** Count the number of bits set within the first 6. */
  int popCount(int bits) {
    if (bits == 0) return 0;
    int popCount = 0;
    for (int i = 0; i < 6; i++) {
      popCount += bits & 1;
      bits >>= 1;
    }
    return popCount;
  }

  int coordPopCount(int x, int y) {
    return popCount(space.get2(x, y));
  }

  void run() {
    flowGraph.drawOnce();
    force.apply(space, next);
    for (int y = 0; y < radius; y++) {
      // Continuous left-to-right feed from the left side.
      next.set2(forceRight, 0, y);
      // wall should bounce back.
      if (y >= wallLo && y <= wallHi) {
        next.set2(forceLeft, wallLeft, y);
      }
    }
    Space2D tmp = space;
    space = next;
    next = tmp;
  }
}
