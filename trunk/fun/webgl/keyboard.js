var activeKeys = new Array();

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
    // Left cursor key
  }
  if (activeKeys[39]) {
    // Right cursor key
  }
  if (activeKeys[38]) {
    // Up cursor key
    zoom++;
  }
  if (activeKeys[40]) {
    // Down cursor key
    zoom--;
  }
}