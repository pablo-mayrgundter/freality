function foo() {
  dX = window.event.x - canvas.offsetLeft - mouseX;
  dY = window.event.y - canvas.offsetTop - mouseY;
  if (dX > dY)
    dY = dX;
  else
    dX = dY;
  var zoom = dX / width;
  regionXOffset += mouseX / width * regionWidth;
  regionYOffset += mouseY / height * regionHeight;
  regionWidth *= zoom;
  regionHeight *= zoom;
}
