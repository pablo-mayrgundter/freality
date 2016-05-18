function mandelbrot() {
  var brushSize = 1;
  var regionWidth = 3, regionHeight = 2.4, regionXOffset = -2, regionYOffset = -1.2;
  var itrMax = 100;
  gfx.fillStyle = 'black';
  gfx.fillRect(0, 0, imageWidth, imageHeight);
  for (var h = 0; h < imageHeight; h++) {
    for (var w = 0; w < imageWidth; w++) {
      var z1 = c1 = ((w / imageWidth) * regionWidth) + regionXOffset;
      var z2 = c2 = ((h / imageHeight) * regionHeight) + regionYOffset;
      for (var itr = 0; itr < itrMax; itr++) {
        var z1_2 = z1*z1;
        var z2_2 = z2*z2;
        var z1_t = z1_2 - z2_2 + c1;
        z2 = 2*z1*z2 + c2;
        z1 = z1_t;
        if ((z1_2 + z2_2) >= 8) {
          var itrVal = itr*4.0;
          var b = Math.floor(itrVal);
          gfx.fillStyle = 'rgb(0,0,'+b+')';
          gfx.fillRect(w, h, brushSize, brushSize);
          break;
        }
      }
    }
  }
}
