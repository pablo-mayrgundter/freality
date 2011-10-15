window.onload = function() {
  if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
    return;
  }
  init(); // in scene.js
  animate();
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

var time = new Date().getTime();

function render() {
  // dt in seconds, since orbital and rotational data are as well.
  var t = new Date().getTime(),
    dt = (t - time) / 1000.0;
  time = t;

  animateSystem(scene, dt); // in animation.js

  if (renderer) {
    renderer.clear();
    renderer.render(scene, camera);
  }
}
