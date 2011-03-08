package phys;

import static phys.SI.*;

class FluidDynamics {
  static {
    Unit<Kilo,Gram> m1 = new Unit<Kilo,Gram>(1.2);
    // Good: incompatible types: Unit<Mega,Meter> m2 = new Unit<Mega,Gram>(2.3);
    Unit<Kilo,Gram> m2 = new Unit<Kilo,Gram>(2);
    m1.mult(m2);
  }
}