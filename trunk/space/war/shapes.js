THREE.EllipseCurve = function(aX, aY, aRadius, eccentricity,
                             aStartAngle, aEndAngle,
                             aClockwise) {

  this.aX = aX;
  this.aY = aY;
  this.aRadius = aRadius;
  this.bRadius = aRadius * Math.sqrt(1.0 - Math.pow(eccentricity, 2.0));
  this.aStartAngle = aStartAngle;
  this.aEndAngle = aEndAngle;
  this.aClockwise = aClockwise;
};

THREE.EllipseCurve.prototype = new THREE.Curve();
THREE.EllipseCurve.prototype.constructor = THREE.EllipseCurve;
THREE.EllipseCurve.prototype.getPoint = function (t) {

  var deltaAngle = this.aEndAngle - this.aStartAngle;

  if (!this.aClockwise) {
    t = 1 - t;
  }

  var angle = this.aStartAngle + t * deltaAngle;

  var tx = this.aX + this.aRadius * Math.cos(angle);
  var ty = this.aY + this.bRadius * Math.sin(angle);

  return new THREE.Vector2(tx, ty);
};
