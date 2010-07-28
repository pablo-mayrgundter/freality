var regionWidth = 2.5;
var regionHeight = 2.5;
var regionXOffset = -2.0;
var regionYOffset = -1.25;
var mouseX, mouseY, dX, dY;
var width;
var height;
var gfx;

function init() {
  var canvas = document.getElementById('canvas');
  width = canvas.clientWidth;
  height = canvas.clientHeight;
  canvas.onmousedown = startDrag;
  canvas.onmouseup = endDrag;
  gfx = canvas.getContext('2d');
  draw();
}

function draw() {
  var itrMax = 1000;
  for (var h = 0; h < height; h++) {
    for (var w = 0; w < width; w++) {
      var z1 = c1 = ((w / width) * regionWidth) + regionXOffset;
      var z2 = c2 = ((h / height) * regionHeight) + regionYOffset;
      var itr = 0;
      for (; itr < itrMax; itr++) {
        var z1_2 = z1*z1;
        var z2_2 = z2*z2;
        var z1_t = z1_2 - z2_2 + c1;
        z2 = 2*z1*z2 + c2;
        z1 = z1_t;
        if ((z1_2 + z2_2) >= 8) {
          var itrVal = itr*4.0;
          var r = 0;//Math.floor(Math.abs(itrVal * z1_2));
          var g = 0;//Math.floor(Math.abs(itrVal * z2_2));
          var b = Math.floor(itrVal);
          gfx.fillStyle = 'rgb(0,0,'+b+')';
          gfx.fillRect(w, h, 1, 1);
          break;
        }
      }
      if (itr == itrMax) {
        gfx.fillStyle = 'black';
        gfx.fillRect(w, h, 1, 1);
      }
    }
  }
}

function startDrag() {
  mouseX = window.event.x - canvas.offsetLeft;
  mouseY = window.event.y - canvas.offsetTop;
}

function endDrag() {
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
  //alert(mouseX+','+mouseY+','+dX+','+dY+', zoom: '+ zoom + ', regionXOffset: '+ regionXOffset +', regionYOffset: '+ regionYOffset);
  draw();
}
