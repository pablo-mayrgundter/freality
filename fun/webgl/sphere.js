function Sphere(radius, texture) {

  this.radius = radius;
  this.texture = texture;
  this.shapeVertices = null;
  this.shapeNormals = null;
  this.shapeTexCoords = null;
  this.shapeVertexIndices = null;

  Sphere.prototype.init = function() {
    this.shapeVertices = gl.createBuffer();
    this.shapeNormals = gl.createBuffer();
    this.shapeTexCoords = gl.createBuffer();
    this.shapeVertexIndices = gl.createBuffer();
    var latitudeBands = 30;
    var longitudeBands = 30;
    var radius = this.radius;
 
    var vertices = [];
    var normals = [];
    var textureCoords = [];
    for (var latNumber = 0; latNumber <= latitudeBands; latNumber++) {
      var theta = latNumber * Math.PI / latitudeBands;
      var sinTheta = Math.sin(theta);
      var cosTheta = Math.cos(theta);
 
      for (var longNumber = 0; longNumber <= longitudeBands; longNumber++) {
        var phi = longNumber * 2 * Math.PI / longitudeBands;
        var sinPhi = Math.sin(phi);
        var cosPhi = Math.cos(phi);
 
        var x = cosPhi * sinTheta;
        var y = cosTheta;
        var z = sinPhi * sinTheta;
        var u = 1 - (longNumber / longitudeBands);
        var v = 1 - (latNumber / latitudeBands);
 
        normals.push(x);
        normals.push(y);
        normals.push(z);
        textureCoords.push(u);
        textureCoords.push(v);
        vertices.push(radius * x);
        vertices.push(radius * y);
        vertices.push(radius * z);
      }
    }
 
    var indexData = [];
    for (var latNumber = 0; latNumber < latitudeBands; latNumber++) {
      for (var longNumber = 0; longNumber < longitudeBands; longNumber++) {
        var first = (latNumber * (longitudeBands + 1)) + longNumber;
        var second = first + longitudeBands + 1;
        indexData.push(first);
        indexData.push(second);
        indexData.push(first + 1);
 
        indexData.push(second);
        indexData.push(second + 1);
        indexData.push(first + 1);
      }
    }
 
    gl.bindBuffer(gl.ARRAY_BUFFER, this.shapeNormals);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(normals), gl.STATIC_DRAW);
    this.shapeNormals.itemSize = 3;
    this.shapeNormals.numItems = normals.length / 3;
 
    gl.bindBuffer(gl.ARRAY_BUFFER, this.shapeTexCoords);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(textureCoords), gl.STATIC_DRAW);
    this.shapeTexCoords.itemSize = 2;
    this.shapeTexCoords.numItems = textureCoords.length / 2;
 
    gl.bindBuffer(gl.ARRAY_BUFFER, this.shapeVertices);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);
    this.shapeVertices.itemSize = 3;
    this.shapeVertices.numItems = vertices.length / 3;
 
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.shapeVertexIndices);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indexData), gl.STATIC_DRAW);
    this.shapeVertexIndices.itemSize = 1;
    this.shapeVertexIndices.numItems = indexData.length;
  }
}
