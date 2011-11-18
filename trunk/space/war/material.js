function loadTexture(texPath) {
  return THREE.ImageUtils.loadTexture(texPath);
}

function pathTexture(filebase, ext) {
  ext = ext || '.jpg';
  return loadTexture('textures/' + filebase + ext);
}

var materials = [];
function cacheMaterial(name) {
  var m = materials[name];
  if (!m) {
    materials[name] = m = new THREE.MeshPhongMaterial({
        color: 0xffffff,
        map: pathTexture(name),
        depthTest: true,
        blending: THREE.AdditiveBlending,
        wireframe: false
      });
  }
  return m;
}
