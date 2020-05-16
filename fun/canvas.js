let imageWidth, imageHeight, x = -2, y = 2, w = 4, h = -4, startX, startY, lastX, lastY, tracking = false;

function initCanvas(drawFn) {
  const canvas = document.getElementById('canvas');
  const overlay = document.getElementById('overlay');
  imageWidth = canvas.clientWidth;
  imageHeight = canvas.clientHeight;
  const gfx = canvas.getContext('2d');
  gfx.clearRect(0, 0, imageWidth, imageHeight);
  const ogfx = overlay.getContext('2d');
  ogfx.strokeStyle = 'white';
  // Bind to document key handlers.
  document.onkeydown = document.onkeyup = e => {
    handleKeyEvent(e, true, gfx, drawFn);
  };
  document.onmousedown = e => {
    tracking = true;
    startX = e.offsetX;
    startY = e.offsetY;
  };
  document.onmousemove = e => {
    if (tracking) {
      const offX = e.offsetX;
      const offY = e.offsetY;
      const offW = offX - startX;
      const offH = offW;
      ogfx.clearRect(0, 0, imageWidth, imageHeight)
      ogfx.strokeRect(startX, startY, offW, offH);
      lastX = offX;
      lastY = offY;
    }
  };
  document.onmouseup = e => {
    ogfx.clearRect(0, 0, imageWidth, imageHeight)
    const offX = e.offsetX;
    const offY = e.offsetY;
    x = x + (startX / imageWidth) * w;
    y = y + (startY / imageHeight) * h;
    w = w * ((offX - startX) / imageWidth);
    h = -w;
    drawFn(gfx, imageWidth, imageHeight, x, y, w, h);
    tracking = false;
  };

  drawFn(gfx, imageWidth, imageHeight, x, y, w, h);
}

// Delegates should set these to function handlers.
let keyUp, keyDown, keyLeft, keyRight;

function handleKeyEvent(e, keyDown, gfx, drawFn) {
  const d = w / 10.0;
  console.log(e.keyCode);
  switch (e.keyCode) {
  case 37: // left
    x -= d;
    break;
  case 38: // up
    y += d;
    break;
  case 39: // right
    x += d;
    break;
  case 40: // down
    y -= d;
    break;
  case 85: // zoom out
    x -= w / 2;
    y -= h / 2;
    w *= 2;
    h *= 2;
  }
  drawFn(gfx, imageWidth, imageHeight, x, y, w, h);
}
