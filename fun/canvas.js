var imageWidth, imageHeight;
var gfx;

function initCanvas() {
  var canvas = document.getElementById('canvas');
  status = document.getElementById('status');
  imageWidth = canvas.clientWidth;
  imageHeight = canvas.clientHeight;
  gfx = canvas.getContext('2d');
  gfx.fillStyle = 'black';
  gfx.fillRect(0, 0, imageWidth, imageHeight);
  gfx.fillStyle = 'rgb(0,0,255)';
  // Bind to document key handlers.
  document.onkeydown = function(e){
    handleKeyEvent(e, true);
  };
  document.onkeyup = function(e){
    handleKeyEvent(e, false);
  };
}

// Delegates should set these to function handlers.
var keyUp, keyDown, keyLeft, keyRight;

function handleKeyEvent(e) {
  switch (e.keyCode) {
  case 37: keyLeft();  break;
  case 38: keyUp();    break;
  case 39: keyRight(); break;
  case 40: keyDown();  break;
  }
}

