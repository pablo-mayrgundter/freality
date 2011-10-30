var time = new Date().getTime();
var timeStepInv = 1000.0;

window.onload = function() {
  if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
    return;
  }
  var container = document.createElement('div');
  document.body.appendChild(container);
  renderer = new THREE.WebGLRenderer({ clearAlpha: 1, clearColor: 0x000000 });
  renderer.setSize(width, height);
  renderer.sortObjects = false;
  renderer.autoClear = false;
  container.appendChild(renderer.domElement);
  scene = new THREE.Scene(); // in shared.js
  init(scene); // in scene.js
  animate();
}

function init(scene) {
  camera = new THREE.TrackballCamera({
      fov: 25,
      aspect: width / height,
      near: 50, // ?
      far: starScale * 100.0, // TODO(pablo): method arg
      rotateSpeed: 1.0,
      zoomSpeed: 1.2,
      panSpeed: 0.2,
      noZoom: false,
      noPan: false,
      staticMoving: false,
      dynamicDampingFactor: 0.3,
      keys: [ 65, 83, 68 ], // [ rotateKey, zoomKey, panKey ],
      domElement: renderer.domElement,
    });
  camera.position.z = 1.0E7; // TODO(pablo): method arg
  window.addEventListener('resize', onWindowResize, false);
}

function onWindowResize(event) {
  width = window.innerWidth;
  height = window.innerHeight;
  renderer.setSize(width, height);
  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  camera.screen.width = width;
  camera.screen.height = height;
  camera.radius = (width + height) / 4;
}

function animate() {
  requestAnimationFrame(animate);
  render();
}

function render() {
  // dt in seconds, since orbital and rotational data are as well.
  var t = new Date().getTime(),
    dt = (t - time) / timeStepInv;
  time = t;

  animateSystem(scene, dt); // in animation.js

  if (renderer) {
    renderer.clear();
    renderer.render(scene, camera);
  }
}
