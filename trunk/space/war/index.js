var time = new Date().getTime();
var timeStepInv = 1E3;

var test_hook = null;

window.onload = function() {
  if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
    return;
  }
  if (test_hook == null) {
    // TODO(pablo): current sets a global since not sure how to wire
    // into angular.
    scene = initCanvas(document.getElementById('scene'), 0);
  } else {
    test_hook();
  }
}

function initCanvas(container, bgColor) {
  if (bgColor == null) {
    bgColor = 0xffffff;
  }
  var width = container.clientWidth;
  var height = container.clientHeight;
  if (width == 0 || height == 0) {
    width = window.innerWidth;
    height = window.innerHeight;
  }
  renderer = new THREE.WebGLRenderer({ clearAlpha: 1, clearColor: bgColor });
  renderer.setSize(width, height);
  renderer.sortObjects = true;
  renderer.autoClear = false;
  container.appendChild(renderer.domElement);
  var scene = new THREE.Scene(); // in shared.js
  var cameraAndControls = init(renderer, scene); // in scene.js
  animate(renderer, cameraAndControls[0], cameraAndControls[1], scene);
  return scene;
}

function init(renderer, scene) {
  // TODO(pablo): pass these as method args
  var near = 0.1;
  var far = 1e7 * 1000;
  var width = renderer.domElement.clientWidth;
  var height = renderer.domElement.clientHeight;
  // TODO(pablo): should not be global.
  camera = new THREE.PerspectiveCamera(25, width / height, near, far);
  camera.rotationAutoUpdate = true;

  var controls = new THREE.TrackballControls(camera, renderer.domElement);
  controls.rotateSpeed = 1.0;
  controls.zoomSpeed = 1.2;
  controls.panSpeed = 0.2;
  controls.noZoom = false;
  controls.noPan = false;
  controls.staticMoving = false;
  controls.dynamicDampingFactor = 0.3;
  controls.minDistance = near;
  //controls.maxDistance = far;
  controls.keys = [ 65, 83, 68 ]; // [ rotateKey, zoomKey, panKey ]
  window.addEventListener('resize',
                          function() { onWindowResize(renderer, camera, controls); },
                          false);

  return [camera, controls];
}

function onWindowResize(renderer, camera, controls) {
  var width = window.innerWidth;
  var height = window.innerHeight;

  renderer.setSize(width, height);

  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  camera.radius = (width + height) / 4;

  controls.screen.width = width;
  controls.screen.height = height;
}

function animate(renderer, camera, controls, scene) {
  requestAnimationFrame(function() { animate(renderer, camera, controls, scene); } );
  render(renderer, camera, controls, scene);
}

function render(renderer, camera, controls, scene) {
  var t = new Date().getTime() * timeScale;
  var dt = t - time;
  var time = t;

  animateSystem(scene, time); // in animation.js

  if (controls) {
    controls.update();
  }
  renderer.clear();
  renderer.render(scene, camera);
}
