'use strict';

var Y_AXIS = new THREE.Vector3(0, 1, 0);

/**
 * Time scale is applied to wall-clock time, so that by a larger time
 * scale will speed things up, 0 will be normal time, negative
 * backwards.
 */
var timeScale = 1;

/**
 * Controlled by UI clicks.. timeScale is basically 2^steps.
 */
var timeScaleSteps = 0;

var time = Date.now();
var lastTime = time;
var dt = time - lastTime;
var simTime = time;
var date = new Date(simTime);

function animation(scene) {
  animateSystem(scene);
  updateView(scene);
}

/**
 * @param delta -1, 0 or 1 for slower, reset or faster.
 */
function changeTimeScale(delta) {
  if (delta == 0) {
    timeScaleSteps = 0;
  } else {
    timeScaleSteps += delta;
  }
  timeScale = (timeScaleSteps < 0 ? -1 : 1) * Math.pow(2, Math.abs(timeScaleSteps));
  var timeScaleElt = document.getElementById('timeScale');
  var msg = '';
  if (timeScaleSteps != 0) {
    msg = '(@ ' + timeScale + ' secs/s)';
  }
  timeScaleElt.innerHTML = msg;
}

/**
 * Recursive animation of orbits and rotations at the current time.
 */
function animateSystem(system) {

  lastTime = time;
  time = Date.now();
  dt = time - lastTime;
  simTime += dt * timeScale;
  date = new Date(simTime);
  var simTimeSecs = simTime / 1000;

  if (system.siderealRotationPeriod) {
    var angle = simTimeSecs / 86400 * twoPi;
    system.setRotationFromAxisAngle(Y_AXIS, angle);
  }

  if (system.orbit) {
    var eccentricity = system.orbit.eccentricity;
    var aRadius = system.orbit.semiMajorAxis * orbitScale;
    var bRadius = aRadius * Math.sqrt(1.0 - Math.pow(eccentricity, 2.0));
    var angle = simTimeSecs / system.orbit.siderealOrbitPeriod * twoPi;
    system.position.set(aRadius * Math.cos(angle), 0, bRadius * Math.sin(angle));
    // if (system.orbit.siderealOrbitPeriod == 31536000) {
    //   console.log('earth angle: ' + angle);
    // }
  }

  for (var ndx in system.children) {
    var child = system.children[ndx];
    animateSystem(child);
  }
}

function updateView() {
  if (targetObj) {
    targetObjLoc.identity();
    var curObj = targetObj;
    var objs = []; // TODO(pablo)
    while (curObj.parent != scene) {
      objs.push(curObj);
      curObj = curObj.parent;
    }
    for (var i = objs.length - 1; i >= 0; i--) {
      var o = objs[i];
      targetObjLoc.multiply(o.matrix);
    }
    targetPos.getPositionFromMatrix(targetObjLoc);
    camera.lookAt(targetPos);
  }
}
