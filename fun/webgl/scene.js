var scenePos = mat4.create();
var sceneView = mat4.create();
var sceneNormals = mat4.create();

var xRot = 0;
var yRot = 0;
var zRot = 0;

function drawScene(time) {
  gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
  gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
  mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 150.0, scenePos);
  mat4.identity(sceneView);

  mat4.translate(sceneView, [0, 0, -10]);

  pushView();
  //mat4.rotate(sceneView, degToRad(xRot++), [1, 0, 0]);
  mat4.rotate(sceneView, degToRad(yRot+=0.05), [0, 1, 0]);
  //mat4.rotate(sceneView, degToRad(zRot++), [0, 0, 1]);
 
  gl.bindBuffer(gl.ARRAY_BUFFER, shapeVertices);
  gl.vertexAttribPointer(shader.aPos, shapeVertices.itemSize, gl.FLOAT, false, 0, 0);
 
  gl.bindBuffer(gl.ARRAY_BUFFER, shapeNormals);
  gl.vertexAttribPointer(shader.aNormal, shapeNormals.itemSize, gl.FLOAT, false, 0, 0);

  gl.bindBuffer(gl.ARRAY_BUFFER, shapeTexCoords);
  gl.vertexAttribPointer(shader.aTex, shapeTexCoords.itemSize, gl.FLOAT, false, 0, 0);
 
  gl.activeTexture(gl.TEXTURE0);
  gl.bindTexture(gl.TEXTURE_2D, theTexture);
  gl.uniform1i(gl.getUniformLocation(shader, 'uSampler'), 0);
 
  gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, shapeVertexIndices);
  setMatrixUniforms();
  gl.drawElements(gl.TRIANGLES, shapeVertexIndices.numItems, gl.UNSIGNED_SHORT, 0);
  popView();
}

function setMatrixUniforms() {
  gl.uniformMatrix4fv(gl.getUniformLocation(shader, 'uPos'), false, scenePos);
  gl.uniformMatrix4fv(gl.getUniformLocation(shader, 'uView'), false, sceneView);
  var normalMatrix = mat3.create();
  mat4.toInverseMat3(sceneView, normalMatrix);
  mat3.transpose(normalMatrix);
  gl.uniformMatrix3fv(gl.getUniformLocation(shader, 'uNormal'), false, normalMatrix);
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
