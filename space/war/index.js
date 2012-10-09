'use strict';

/**
 * Solar System simulator inspired by Celestia using Three.js.
 *
 * TODO:
 * - Q/A against Celestia.
 *   - Add actual epoch-based locations.
 *   - Add actual epoch-based locations.
 * - Incremental scene-graph loading.
 * - View options, e.g. toggle orbits.
 * - Finish support for permalinks.
 * - Stars and Galaxies.
 * - Time slider.
 * - Zoom in.
 *
 * @see http://shatters.net/celestia/
 * @see http://mrdoob.github.com/three.js/
 * @author Pablo Mayrgundter
 */

var time = new Date().getTime();

var test_hook = null;
var animationDelegate = animation;
var ctrl = null;
var scene = null;
var camera = null;

window.onload = function() {
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
  var renderer = new THREE.WebGLRenderer({
      antialias: true,
      clearAlpha: 1,
      clearColor: bgColor });
  renderer.setSize(width, height);
  renderer.sortObjects = true;
  renderer.autoClear = true;
  container.appendChild(renderer.domElement);
  var scene = new THREE.Scene();
  var cameraAndControls = initCameraAndControls(renderer);
  animate(renderer, cameraAndControls[0], cameraAndControls[1], scene);
  // This starts the scene loading process..
  ctrl = new Controller();
  return scene;
}

function initCameraAndControls(renderer) {

  // TODO(pablo): pass these as method args
  var width = renderer.domElement.clientWidth;
  var height = renderer.domElement.clientHeight;
  // TODO(pablo): should not be global.
  camera = new THREE.PerspectiveCamera(25, width / height, 1, 1E13);
  camera.rotationAutoUpdate = true;

  var controls = new THREE.TrackballControls(camera, renderer.domElement);
  controls.rotateSpeed = 0.0005;
  controls.zoomSpeed = 0.0001;
  controls.panSpeed = 0.01;
  controls.noZoom = false;
  controls.noPan = false;
  controls.staticMoving = false;
  controls.dynamicDampingFactor = 0.5;
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

  if (controls) {
    controls.screen.width = width;
    controls.screen.height = height;
  }
}

function animate(renderer, camera, controls, scene) {
  requestAnimationFrame(function() { animate(renderer, camera, controls, scene); } );
  render(renderer, camera, controls, scene);
}

var targetObj = null;
var targetObjLoc = new THREE.Matrix4;

function render(renderer, camera, controls, scene) {
  var t = new Date().getTime() * timeScale;
  var dt = t - time;
  var time = t;

  if (controls) {
    controls.update();
  }

  if (animationDelegate) {
    animationDelegate(renderer, camera, controls, scene);
  }

  renderer.clear();
  renderer.render(scene, camera);
}
