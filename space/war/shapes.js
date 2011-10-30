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

// GRID

function grid() {
  var params = {
    stepSize: 3E8,
    numSteps: 1E2,
    color: 0x111155,
    lineWidth: 1
  };
  return lineGrid(params);
}

/**
 * Creates a shape with 3 reference grids, xy, xz and yz.
 *
 * TODO(pablo): each grid has its own geometry.
 */
function lineGrid(params) {

  var grids = new THREE.Object3D();

  var size = params.stepSize * params.numSteps;
  var mat = new THREE.LineBasicMaterial({color: params.color,
                                         lineWidth: params.lineWidth,
                                         blending: THREE.AdditiveBlending,
                                         transparent: true});

  var xyGrid = new THREE.Line(gridGeometry(params), mat);
  xyGrid.position.x -= size / 2;
  xyGrid.position.y -= size / 2;

  var xzGrid = new THREE.Line(gridGeometry(params), mat);
  xzGrid.rotation.x = Math.PI / 2;
  xzGrid.position.x -= size / 2;
  xzGrid.position.z -= size / 2;

  var yzGrid = new THREE.Line(gridGeometry(params), mat);
  yzGrid.rotation.y = Math.PI / 2;
  yzGrid.position.z += size / 2;
  yzGrid.position.y -= size / 2;

  grids.addChild(xzGrid);
  grids.addChild(yzGrid);

  grids.addChild(xyGrid);

  return grids;
}

function gridGeometry(params) {
  if (!params) params = {}
  if (!params.stepSize) params.stepSize = 1
  if (!params.numSteps) params.numSteps = 10;
  var gridGeom = new THREE.Geometry();
  var size = params.stepSize * params.numSteps;
  for (var x = 0; x < params.numSteps; x += 2) {
    var xOff = x * params.stepSize;
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(xOff, 0, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(xOff, size, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(xOff + params.stepSize, size, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(xOff + params.stepSize, 0, 0)));
  }
  for (var y = 0; y < params.numSteps; y += 2) {
    var yOff = y * params.stepSize;
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(0, yOff, 0, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(size, yOff, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(size, yOff + params.stepSize, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(0, yOff + params.stepSize, 0)));
  }
  if (params.numSteps % 2 == 0) {
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(0, size, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(size, size, 0)));
    gridGeom.vertices.push(new THREE.Vertex(new THREE.Vector3(size, 0, 0)));
  }
  return gridGeom;
}

function imgGrid(params) {

  var imageCanvas = document.createElement('canvas'),
    context = imageCanvas.getContext('2d');

  imageCanvas.width = imageCanvas.height = 32;

  context.strokeStyle = '#' + params.color.toString(16);
  context.lineWidth = params.lineWidth;
  context.strokeRect(0, 0, 32, 32);

  var textureCanvas =
    new THREE.Texture(imageCanvas, THREE.UVMapping, THREE.RepeatWrapping, THREE.RepeatWrapping);
  materialCanvas = new THREE.MeshBasicMaterial({map: textureCanvas});

  var span = params.stepSize * params.numSteps;

  textureCanvas.needsUpdate = true;
  textureCanvas.repeat.set(params.numSteps, params.numSteps);

  var geometry = new THREE.PlaneGeometry(1, 1);
  var meshCanvas = new THREE.Mesh(geometry, materialCanvas);
  meshCanvas.scale.set(span, span, span);
  meshCanvas.doubleSided = true;

  return meshCanvas;
}
