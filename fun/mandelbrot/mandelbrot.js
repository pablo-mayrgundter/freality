function mandelbrot(gfx, imageWidth, imageHeight, x, y, w, h) {
  console.log(`${imageWidth},${imageHeight},${x},${y},${w},${h}`);
  const itrMax = 1000;
  gfx.fillStyle = 'black';
  gfx.fillRect(0, 0, imageWidth, imageHeight);
  const img = gfx.getImageData(0, 0, imageWidth, imageHeight);
  for (let imgRow = 0; imgRow < imageHeight; imgRow++) {
    for (let imgCol = 0; imgCol < imageWidth; imgCol++) {
      let z1 = c1 = x + ((imgCol / imageWidth) * w);
      let z2 = c2 = y + ((imgRow / imageHeight) * h);
      const ndx = (4 * imgCol) + (4 * imgRow * img.width);
      let escaped = false;
      for (let itr = 0; itr < itrMax; itr++) {
        const z1_2 = z1*z1;
        const z2_2 = z2*z2;
        const z1_t = z1_2 - z2_2 + c1;
        z2 = 2*z1*z2 + c2;
        z1 = z1_t;
        if ((z1_2 + z2_2) >= 4) {
          const brightness = Math.floor(itr * 5);
          img.data[ndx + 2] = brightness;
          if (itr % 2 == 0) {
            img.data[ndx + 2] = brightness;
          } else {
            img.data[ndx + 1] = brightness;
          }
          escaped = true;
          break;
        }
      }
      if (!escaped) {
        img.data[ndx] = 1; // mark for below.
      }
    }
  }
  gfx.putImageData(img, 0, 0);
  anim = () => {
      for (let imgRow = 0; imgRow < imageHeight; imgRow++) {
        for (let imgCol = 0; imgCol < imageWidth; imgCol++) {
          const ndx = (4 * imgCol) + (4 * imgRow * img.width);
          if (img.data[ndx] == 1) {
            continue;
          }
          const tmp = img.data[ndx + 2];
          img.data[ndx + 2] = img.data[ndx + 1];
          img.data[ndx + 1] = tmp;
        }
      }
      gfx.putImageData(img, 0, 0);
      requestAnimationFrame(anim);
  };
  //anim();
}
