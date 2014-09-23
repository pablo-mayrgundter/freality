var scene;
var camera;
var renderer;
var controls;

var init = function() {
  scene = new THREE.Scene();
  camera = new THREE.PerspectiveCamera(90,
                                       window.innerWidth / window.innerHeight,
                                       0.1,
                                       10000);

  renderer = new THREE.WebGLRenderer({alpha: true});
  renderer.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(renderer.domElement);

  camera.position.z = 500;
  camera.position.y = 500;
  camera.rotation.x = Math.PI / 4;
  controls = new THREE.OrbitControls(camera, renderer.domElement);

  scene.fog = new THREE.FogExp2(0x000055, 0.0003);
}

var geometry = new THREE.BoxGeometry(1,1,1);

var materialPalette = [];
for (var i = 0; i < 10; i++) {
  // Using wireframe materials to illustrate shape details.
  var c = new THREE.Color(Math.random(), Math.random(), Math.random()).getHex();
  var darkMaterial = new THREE.MeshBasicMaterial({
      color: c
    });
  var wireframeMaterial = new THREE.MeshBasicMaterial({
      color: c | 0x3f3f3f,
      wireframe: true,
      transparent: true
    }); 
  var multiMaterial = [darkMaterial, wireframeMaterial]; 
  materialPalette.push(multiMaterial);
}

var tori = [];
function addTorus(args) {
  args = args || {};
  args.pos = args.pos || [0,0,0];
  args.rot = args.rot || [0,0,0];
  var torus = THREE.SceneUtils.createMultiMaterialObject( 
      // radius of entire torus, diameter of tube (less than total radius), 
      // segments around radius, segments around torus ("sides")
      new THREE.TorusGeometry(args.radius || 25,
                              args.diameter || 3,
                              args.surfaceRes || 8,
                              args.torusRes || 5),
      materialPalette[Math.floor(Math.random() * materialPalette.length)]);
  torus.position.fromArray(args.pos);
  torus.rotation.fromArray(args.rot);
  torus.spin = args.spin || 0.1;
  torus.links = 0;
  tori.push(torus);
  scene.add(torus);
}

var lineGeometry = {vertices: []};

function render() {
  requestAnimationFrame(render);

  renderer.render(scene, camera);

  for (var ndx in tori) {
    var t = tori[ndx];
    var speed = 0.01;
    if (t.links > 0) {
      speed /= t.links;
    }
    t.rotation.z += t.spin * 0.1 * t.links * 10;
    t.position.x += Math.sin(t.position.y * speed);
    t.position.y += Math.cos(t.position.z * speed) + Math.sin(t.position.x * speed);
    t.position.z += Math.sin(t.position.x * speed);
  }

  updateLasers();

  controls.update();
}

var laserGeoms = [];
var updateLasers = function() {
  for (var i = 0; i < laserGeoms.length; i++) {
    var laserGeom = laserGeoms[i];

    // Move first vertex of line to center of its torus.
    var startVert = laserGeom.vertices[0];
    var torus = tori[i];
    startVert.x = torus.position.x;
    startVert.y = torus.position.y;
    startVert.z = torus.position.z;

    // Then find the nearest torus to connect to.
    var nearestTorus;
    var nearestTorusDist = 100000000.0;
    var startVec = new THREE.Vector3(startVert.x,
                                     startVert.y,
                                     startVert.z);
    for (var j = 0; j < tori.length; j++) {
      var neighborTorus = tori[j];
      if (neighborTorus == torus || neighborTorus.link == torus) {
        continue;
      }
      var dist = startVec.distanceTo(
          new THREE.Vector3(neighborTorus.position.x,
                            neighborTorus.position.y,
                            neighborTorus.position.z));
      if (dist < nearestTorusDist) {
        nearestTorusDist = dist;
        nearestTorus = neighborTorus;
      }
    }
    var endVert = laserGeom.vertices[1];
    if (torus.link) {
      torus.link.links--;
    }
    torus.link = nearestTorus;
    nearestTorus.links++;

    endVert.x = nearestTorus.position.x;
    endVert.y = nearestTorus.position.y;
    endVert.z = nearestTorus.position.z;

    laserGeom.verticesNeedUpdate = true;
  }
}

init();
render();

function addCraft(args) {
  args = args || {};
  args.rot = args.rot || [Math.PI / 2, 0, 0];
  args.pos = args.pos || [0,0,0];

  addTorus(args);

  args.radius = 15;
  args.spin = -0.1;

  addTorus(args);
}


var laserMaterial = new THREE.LineBasicMaterial({
    color: 0xff5555,
    linewidth: 3,
    linecap: 'square'
  });
function lasers() {
  for (var i = 0; i < tori.length; i++) {
    var t = tori[i];
    var laserGeometry = new THREE.Geometry();
    laserGeometry.dynamic = true;
    laserGeoms.push(laserGeometry);
    var verts = laserGeometry.vertices;
    // twice, one will move later.
    verts.push(new THREE.Vector3(t.position.x, t.position.y, t.position.z));
    verts.push(new THREE.Vector3(t.position.x, t.position.y, t.position.z));
    laserGeometry.computeLineDistances();
    scene.add(new THREE.Line(laserGeometry, laserMaterial));
  }
}

var scale = 500;
var scaleHalf = scale / 2.0;
for (var i = 0; i < 50; i += 1) {
  var x = (i - 50) * Math.PI / 200;
  var args = {};
  args.pos = [Math.sin(x) * scale - scaleHalf,
              Math.cos(x) * scale - scaleHalf,
              Math.cos(x) * scale - scaleHalf];
  args.torusRes = 3 + Math.floor(Math.random() * 5.0);
  addCraft(args);
}
lasers();
