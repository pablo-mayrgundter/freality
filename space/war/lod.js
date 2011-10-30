function lod() {
  var material = new THREE.MeshLambertMaterial({color: 0xffffff, wireframe: true });

  var geometry =
    [[new THREE.SphereGeometry(100, 64, 32), 300],
     [new THREE.SphereGeometry(100, 32, 16), 1000],
     [new THREE.SphereGeometry(100, 16, 8), 2000],
     [new THREE.SphereGeometry(100, 8, 4), 10000]];

  var lod = new THREE.LOD();
  for (var j = 0; j < 1000; j++) {
    for (var i = 0; i < geometry.length; i++) {
      var mesh = new THREE.Mesh(geometry[i][0], material);
      mesh.scale.set(1.5, 1.5, 1.5);
      mesh.updateMatrix();
      mesh.matrixAutoUpdate = false;
      lod.addLevel(mesh, geometry[i][1]);
    }

    lod.position.x = 10000 * (0.5 - Math.random());
    lod.position.y =  7500 * (0.5 - Math.random());
    lod.position.z = 10000 * (0.5 - Math.random());
    lod.updateMatrix();
    lod.matrixAutoUpdate = false;
  }
  return lod;
}