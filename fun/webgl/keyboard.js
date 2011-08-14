var activeKeys = new Array();
var dTrans = 0.1;

function initKeyHandler() {
  document.onkeydown = handleKeyDown;
  document.onkeyup = handleKeyUp;
}

function handleKeyDown(event) {
  activeKeys[event.keyCode] = true;
}

function handleKeyUp(event) {
  activeKeys[event.keyCode] = false;
}

function handleKeys() {
  if (activeKeys[37]) {
    strafe += dTrans;
  }
  if (activeKeys[39]) {
    strafe -= dTrans;
  }
  if (activeKeys[38]) {
    // Up cursor key
    pitch += dTrans;
  }
  if (activeKeys[40]) {
    // Down cursor key
    pitch -= dTrans;
  }
}