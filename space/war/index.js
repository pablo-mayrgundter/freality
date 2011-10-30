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
  radius = 1e7; // TODO(pablo): method arg
  far = radius * 1000;
  camera = new THREE.PerspectiveCamera(25, width / height, 50, far);
  camera.position.z = radius; 

  controls = new THREE.TrackballControls(camera, renderer.domElement);
  controls.rotateSpeed = 1.0;
  controls.zoomSpeed = 1.2;
  controls.panSpeed = 0.2;
  controls.noZoom = false;
  controls.noPan = false;
  controls.staticMoving = false;
  controls.dynamicDampingFactor = 0.3;
  controls.minDistance = 10;
  controls.maxDistance = far;
  controls.keys = [ 65, 83, 68 ]; // [ rotateKey, zoomKey, panKey ]
  window.addEventListener('resize', onWindowResize, false);
}

function onWindowResize(event) {
  width = window.innerWidth;
  height = window.innerHeight;

  renderer.setSize(width, height);

  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  camera.radius = (width + height) / 4;

  controls.screen.width = width;
  controls.screen.height = height;
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

  if (controls && renderer) {
    controls.update();
    renderer.clear();
    renderer.render(scene, camera);
  }
}
