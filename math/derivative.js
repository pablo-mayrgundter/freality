// Practicing https://en.wikipedia.org/wiki/Calculus#Differential_calculus
class Derivative {
  constructor(f) {
    this.f = f;
  }

  slope(x) {
    const f_a = f(a);
    for (let i = 1; i <= 10; i++) {
      const h = 1 / Math.pow(2, i);
      const y = f(a + h) - f(a) / h;
    }
  }
}