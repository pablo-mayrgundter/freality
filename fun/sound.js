const uniforms = {
  iResolution: {
    type: "v3",
    value: new THREE.Vector2()
  },
  iTime: {
    type: "f",
    value: 1.0
  },
  iTimeDelta: {
    type: "f",
    value: 1.0
  },
  iMouse: {
    type: "v2",
    value: new THREE.Vector2()
  },
  iTime: {
    type: "i",
    value: 8
  }
};

document.onmousemove = (e) => {
  e = e || window.event;
  uniforms.iMouse.value.x = e.clientX;
  uniforms.iMouse.value.y = e.clientY;
};

function init(item) {
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);
  camera.position.x = 0;
  camera.position.y = 0;
  camera.position.z = 970;
  const webgl = new THREE.WebGLRenderer();
  webgl.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(webgl.domElement);

  function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
  }
  window.addEventListener('resize', onWindowResize, false);
  renderer = webgl;
  const light = new THREE.HemisphereLight(0xeeeeee, 0x888888, 1);
  light.position.set(0, 20, 0);
  scene.add(light);

  function render() {
    item.update();
    renderer.render(scene, camera);
    requestAnimationFrame(render);
  }
  scene.add(item.obj);
  render();
}

function GameObject() {
  this.geometry = new THREE.PlaneGeometry(window.innerWidth, window.innerHeight, 1);
  uniforms.iResolution.value.x = window.innerWidth;
  uniforms.iResolution.value.y = window.innerHeight;
  this.material = new THREE.ShaderMaterial({
      uniforms: uniforms,
      vertexShader: document.getElementById('main').textContent,
      fragmentShader: document.getElementById('frag1').textContent
    });
  this.obj = new THREE.Mesh(this.geometry, this.material);
  this.obj.startTime = Date.now();
  this.obj.uniforms = uniforms;
}

GameObject.prototype.update = function() {
  const elapsedMilliseconds = Date.now() - this.obj.startTime;
  const elapsedSeconds = elapsedMilliseconds / 1000.;
  this.obj.uniforms.iTime.value = elapsedSeconds;
};

init(new GameObject());
