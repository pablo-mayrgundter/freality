function Texture(src) {
  this.theTexture = null;
  this.texSource = src;

  Texture.prototype.init = function() {
    this.theTexture = gl.createTexture();
    this.theTexture.image = new Image();
    var me = this;
    this.theTexture.image.onload = function () {
      me.handleLoad(me.theTexture)
    }
 
    this.theTexture.image.src = this.texSource;
  }

  Texture.prototype.handleLoad = function(texture) {
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, texture.image);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
    gl.bindTexture(gl.TEXTURE_2D, null);
  }
}
