var scenePos = mat4.create();
var sceneView = mat4.create();
var sceneNormals = mat4.create();

var strafe = 0;
var zoom = -10;
var pitch = 0;
var yaw = 0;
var dRot = 0.1;

var yRot = 0;

function drawScene(time) {
  gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
  gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
  gl.uniformMatrix4fv(shader.uPos, false, scenePos);
  gl.uniformMatrix4fv(shader.uView, false, sceneView);

  var normalMatrix = mat3.create();
  mat4.toInverseMat3(sceneView, normalMatrix);
  mat3.transpose(normalMatrix);
  gl.uniformMatrix3fv(shader.uNormal, false, normalMatrix);

  mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 150.0, scenePos);
  mat4.identity(sceneView);

  // Camera control
  mat4.rotate(sceneView, degToRad(-pitch), [1, 0, 0]);
  mat4.multiply(sceneView, mouseRotation);
  mat4.translate(sceneView, [strafe, 0, zoom]);
  mat4.rotate(sceneView, degToRad(yRot += dRot), [0, 1, 0]);

  // Ordered with opaque earth first, then transparent atmosphere
  // second.  Blending is switched on at atmos.
  var shapes = [earth, atmos];
  for (var i in shapes) {
    var shape = shapes[i];
    if (shape == atmos) {
      //gl.disable(gl.DEPTH_TEST);
      gl.enable(gl.BLEND);
      gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
    }
    gl.bindBuffer(gl.ARRAY_BUFFER, shape.shapeVertices);
    gl.vertexAttribPointer(shader.aPos, shape.shapeVertices.itemSize, gl.FLOAT, false, 0, 0);
 
    gl.bindBuffer(gl.ARRAY_BUFFER, shape.shapeNormals);
    gl.vertexAttribPointer(shader.aNormal, shape.shapeNormals.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, shape.shapeTexCoords);
    gl.vertexAttribPointer(shader.aTex, shape.shapeTexCoords.itemSize, gl.FLOAT, false, 0, 0);
 
    gl.activeTexture(gl.TEXTURE0);
    gl.bindTexture(gl.TEXTURE_2D, shape.texture.theTexture);
    gl.uniform1i(shader.uSampler, 0);
 
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, shape.shapeVertexIndices);
    gl.drawElements(gl.TRIANGLES, shape.shapeVertexIndices.numItems, gl.UNSIGNED_SHORT, 0);
    gl.uniform1i(shader.uSampler, 0);
  }
}

var viewStack = new Array();

function pushView() {
  var copy = mat4.create();
  mat4.set(sceneView, copy);
  viewStack.push(copy);
}

function popView() {
  if (viewStack.length == 0) {
    throw 'Cannot push empty stack.';
  }
  sceneView = viewStack.pop();
}

function degToRad(deg) {
  return deg * Math.PI / 180;
}
