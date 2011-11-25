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
        map: pathTexture(name),
      });
  }
  return m;
}
