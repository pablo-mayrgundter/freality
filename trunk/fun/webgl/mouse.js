var mouseDown = false;
var lastMouseX = null;
var lastMouseY = null;
 
var mouseRotation = mat4.create();
mat4.identity(mouseRotation);

function initMouseHandler() {
  canvas.onmousedown = handleMouseDown;
  window.onmouseup = handleMouseUp;
  window.onmousemove = handleMouseMove;
}
 
function handleMouseDown(event) {
  mouseDown = true;
  lastMouseX = event.clientX;
  lastMouseY = event.clientY;
}
 
function handleMouseUp(event) {
  mouseDown = false;
}
 
function handleMouseMove(event) {
  if (!mouseDown) {
    return;
  }
  var newX = event.clientX;
  var newY = event.clientY;
 
  var deltaX = newX - lastMouseX;
  var newMouseRotation = mat4.create();
  mat4.identity(newMouseRotation);
  mat4.rotate(newMouseRotation, degToRad(deltaX / 10), [0, 1, 0]);
 
  var deltaY = newY - lastMouseY;
  mat4.rotate(newMouseRotation, degToRad(deltaY / 10), [1, 0, 0]);
 
  mat4.multiply(newMouseRotation, mouseRotation, mouseRotation);
  lastMouseX = newX;
  lastMouseY = newY;
}
