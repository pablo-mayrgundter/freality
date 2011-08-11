function initShaders() {
  var pixelShader =
    loadShader($('pixel_shader').innerHTML, $('pixel_shader').type);
  var vertexShader =
    loadShader($('vertex_shader').innerHTML, $('vertex_shader').type);
  if (pixelShader == null || vertexShader == null) {
    console.log('Could not load shaders.');
    return;
  }
  shader = gl.createProgram();
  gl.attachShader(shader, pixelShader);
  gl.attachShader(shader, vertexShader);
  gl.linkProgram(shader);

  if (!gl.getProgramParameter(shader, gl.LINK_STATUS)) {
    alert('Shader link error: '+ gl.getShaderInfoLog(shader));
    return;
  }

  gl.useProgram(shader);

  shader.aPos = gl.getAttribLocation(shader, 'aPos');
  gl.enableVertexAttribArray(shader.aPos);
  shader.uPos = gl.getUniformLocation(shader, 'uPos');
  shader.uView = gl.getUniformLocation(shader, 'uView');
  shader.uNormal = gl.getUniformLocation(shader, 'uNormal');

  shader.aTex = gl.getAttribLocation(shader, 'aTex');
  gl.enableVertexAttribArray(shader.aTex);

  shader.aNormal = gl.getAttribLocation(shader, 'aNormal');
  gl.enableVertexAttribArray(shader.aNormal);

  shader.uSampler = gl.getUniformLocation(shader, 'uSampler');
}

/**
 * @param id The DOM ID of the shader script.
 * @return null If the shader could not be loaded.
 */
function loadShader(src, type) {
  var shader;
  if (type == 'x-shader/x-fragment') {
    shader = gl.createShader(gl.FRAGMENT_SHADER);
  } else if (type == 'x-shader/x-vertex') {
    shader = gl.createShader(gl.VERTEX_SHADER);
  } else {
    return null;
  }

  gl.shaderSource(shader, src);
  gl.compileShader(shader);

  if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
    alert('Shader compilation error: '+ gl.getShaderInfoLog(shader));
    return null;
  }

  return shader;
}
