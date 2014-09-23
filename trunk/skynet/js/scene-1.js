var scene;
var camera;
var renderer;
var controls;

function init() {
  scene = new THREE.Scene();
  camera = new THREE.PerspectiveCamera(90,
                                       window.innerWidth / window.innerHeight,
                                       0.1,
                                       10000);

  renderer = new THREE.WebGLRenderer();
  renderer.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(renderer.domElement);

  camera.position.z = 500;
  camera.position.y = 500;
  camera.rotation.x = Math.PI / 4;
  controls = new THREE.OrbitControls(camera, renderer.domElement);

  scene.fog = new THREE.FogExp2(0x9999ff, 0.001);
}

var geometry = new THREE.BoxGeometry(1,1,1);
var material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );

// Using wireframe materials to illustrate shape details.
var darkMaterial = new THREE.MeshBasicMaterial( { color: 0xffffcc } );
var wireframeMaterial = new THREE.MeshBasicMaterial( { color: 0x000000, wireframe: true, transparent: true } ); 
var multiMaterial = [ darkMaterial, wireframeMaterial ]; 


var cubes = [];
function addCube(x, y, z) {
  var cube = new THREE.Mesh( geometry, material );
  cubes.push(cube);
  scene.add(cube);
  cube.position.set(x || 0, y || 0, z || 0);
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
                              args.torusRes || 20),
      multiMaterial);
  torus.position.fromArray(args.pos);
  torus.rotation.fromArray(args.rot);
  torus.spin = args.spin || 0.1;
  tori.push(torus);
  scene.add(torus);
}

var time = 0;
var lineGeometry = {vertices: []};

function render() {
  requestAnimationFrame(render);
  renderer.render(scene, camera);
  for (var ndx in tori) {
    var t = tori[ndx];
    t.rotation.z += t.spin;

    t.position.x += Math.sin(t.position.y * 0.01);
    t.position.y += Math.cos(t.position.z * 0.01);
    t.position.z += Math.sin(t.position.x * 0.01);
  }

  var verts = lineGeometry.vertices;
  for (var i = 0; i < verts.length; i++) {
    var v = verts[i];
    var t = tori[i];
    if (t) {
      v.x = t.position.x;
      v.y = t.position.y;
      v.z = t.position.z;
    }
  }
  lineGeometry.verticesNeedUpdate = true;

  controls.update();
  time += 0.001;
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

var lineMaterial = new THREE.LineBasicMaterial( { color: 0xcc0000 } );
function laser() {
  var last = tori[0];
  lineGeometry = new THREE.Geometry();
  lineGeometry.dynamic = true;
  var vertArray = lineGeometry.vertices;
  for (var i = 0; i < tori.length; i++) {
    var t = tori[i];
    vertArray.push(new THREE.Vector3(t.position.x, t.position.y, t.position.z));
  }
  lineGeometry.computeLineDistances();
  var line = new THREE.Line(lineGeometry, lineMaterial);
  scene.add(line);
}

var scale = 500;
var scaleHalf = scale / 2.0;
for (var i = 0; i < 100; i++) {
  var x = (i - 50) * Math.PI / 200;
  addCraft({pos: [Math.sin(x) * scale - scaleHalf,
                  Math.cos(x) * scale - scaleHalf,
                  Math.cos(x) * scale - scaleHalf]});
}
laser();
