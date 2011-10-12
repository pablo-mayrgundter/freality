angular.service('Planet', function($resource) {
    return $resource('data/:name.json');
  });

var sceneNodes = [];

function PlanetRsrc(Planet) {

  this.planet = {};

  this.load = function(planetName) {
    if (!planetName) {
      console.log('PlanetRsrc: load: ERROR, missing planet name.');
      return;
    }
    console.log('Loading: ' + planetName);
    planetName = planetName.toLowerCase();
    if (sceneNodes[planetName]) {
      this.select(sceneNodes[planetName]);
      return;
    }
    var me = this;
    return Planet.get({name:planetName},
                      function(p){ me.display(p); });
  };

  this.select = function(node) {
    console.log('Camera to: ' + node.planet.name);
    camera.position.x = node.position.x;
    camera.position.y = node.position.y;
    camera.position.z = node.position.z;
    //camera.target = node;
  };

  this.display = function(p) {
    this.planet = p;
    // Fix up wire notation.
    // TODO(pablo): shouldn't need to do this.
    p.radius = p.radius.match(/\d+(.\d+)?/)[0];
    p.radius = Math.log(p.radius);
    p.siderealRotationPeriod = p.siderealRotationPeriod.match(/\d+(.\d+)?/)[0];
    p.axialInclination = p.axialInclination / 360.0 * 2.0 * Math.PI;

    var parentNode = sceneNodes[p.parent];
    var obj = displayPlanet(parentNode, p);
    if (!root) {
      root = obj;
      scene.addObject(obj);
      camera.position.z = p.radius * 7;
    }
    sceneNodes[p.name] = obj;

    if (p.orbit) {
      p.orbit.semiMajorAxis = parseFloat(p.orbit.semiMajorAxis);
      p.orbit.siderealOrbitPeriod = parseFloat(p.orbit.siderealOrbitPeriod);
      var parentSemiMajorAxis = 0;
      if (parentNode) {
        if (parentNode.planet.orbit) {
          parentSemiMajorAxis = parentNode.planet.orbit.semiMajorAxis;
          console.log('getting xOff from: ' + parentNode.planet.name + ', equals: '+ parentSemiMajorAxis);
        }
      }
      displayOrbit(parentNode, p.orbit, parentSemiMajorAxis);
    }
    if (p.system) {
      for (s in p.system) {
        var name = p.system[s];
        var me = this;
        Planet.get({name:name},
                      function(p){ me.display(p); });
      }
    }
  };

  this.$watch('name', 'load(name)');
}

var atmosScale = 1.005,
  maxOrbit = 10000000000.0,
//  maxOrbit = 100000000.0,
  orbitScale = 1.0 / maxOrbit * (maxOrbit / 10000.0),
  starScale = 1.0 / orbitScale * 10.0,
  timeScale = 0.1,
  height = window.innerHeight,
  width  = window.innerWidth,
  container,
  camera, scene, renderer,
  root, globe,
  time = new Date().getTime();

function init() {
  container = document.createElement('div');
  document.body.appendChild(container);

  scene = new THREE.Scene();

  renderer = new THREE.WebGLRenderer({ clearAlpha: 1, clearColor: 0x000000 });
  renderer.setSize(width, height);
  renderer.sortObjects = false;
  renderer.autoClear = false;
  container.appendChild(renderer.domElement);

  camera = new THREE.TrackballCamera({
      fov: 25,
      aspect: width / height,
      near: 50,
      far: starScale * 100.0,
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

  camera.position.z = 1;

  var dirLight = new THREE.DirectionalLight(0xFFFFFF);
  dirLight.position.set(-1, 0, 1);
  dirLight.position.normalize();
  scene.addLight(dirLight);

  // stars
  var i,
    vector,
    starsGeometry = new THREE.Geometry();
  for (i = 0; i < 1500; i++) {
    vector = new THREE.Vector3(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
    vector.multiplyScalar(starScale);
    starsGeometry.vertices.push(new THREE.Vertex(vector));
  }
  var stars,
    starsMaterials = [new THREE.ParticleBasicMaterial({ color: 0xFFFFFF, size: 2, sizeAttenuation: false }),
                      new THREE.ParticleBasicMaterial({ color: 0xFFFFFF, size: 1, sizeAttenuation: false }),
                      new THREE.ParticleBasicMaterial({ color: 0xCCCCCC, size: 2, sizeAttenuation: false }),
                      new THREE.ParticleBasicMaterial({ color: 0xCCCCCC, size: 1, sizeAttenuation: false }),
                      new THREE.ParticleBasicMaterial({ color: 0xAAAAAA, size: 2, sizeAttenuation: false }),
                      new THREE.ParticleBasicMaterial({ color: 0xAAAAAA, size: 1, sizeAttenuation: false })];
  for (i = 10; i < 30; i++) {
    stars = new THREE.ParticleSystem(starsGeometry, starsMaterials[ i % 6 ]);
    stars.rotation.x = Math.random() * 6;
    stars.rotation.y = Math.random() * 6;
    stars.rotation.z = Math.random() * 6;
    var s = i * 10;
    stars.scale.set(s, s, s);
    stars.matrixAutoUpdate = false;
    stars.updateMatrix();
    scene.addObject(stars);
  }

  window.addEventListener('resize', onWindowResize, false);
}

function displayPlanet(parentNode, planet) {
  console.log('displayPlanet: '+ planet.name);
  var globe = new THREE.Object3D;
  if (parentNode) {
    parentNode.addChild(globe);
  }
  globe.planet = planet;

  var xOff = 0;
  if (globe.planet.orbit) {
    xOff = globe.planet.orbit.semiMajorAxis * orbitScale;
    console.log('planet xOff: '+ xOff);
  }
  globe.position.set(xOff, 0, 0);

  // planet
  var geometry = new THREE.SphereGeometry(planet.radius, 100, 50);
  geometry.computeTangents();

  var planetMaterial;
  if (false) {
    planetMaterial = new THREE.LineBasicMaterial({color: 0x0000ff});
  } else if (!(planet.texture_hydrosphere || planet.texture_terrain)) {
    var planetTexture = THREE.ImageUtils.loadTexture('textures/' + planet.name + '.jpg');
    planetMaterial = new THREE.MeshPhongMaterial({color: 0xffffff, map: planetTexture});
  } else {
    var planetTexture = THREE.ImageUtils.loadTexture('textures/' + planet.name + '.jpg');
    // Fancy planets.
    var shader = THREE.ShaderUtils.lib['normal'],
      uniforms = THREE.UniformsUtils.clone(shader.uniforms);

    uniforms['tDiffuse'].texture = planetTexture;
    uniforms['enableAO'].value = false;
    uniforms['enableDiffuse'].value = true;
    uniforms['uDiffuseColor'].value.setHex(0xFFFFFF);
    uniforms['uAmbientColor'].value.setHex(0x000000);
    uniforms['uShininess'].value = 30;

    if (planet.texture_hydrosphere) {
      uniforms['enableSpecular'].value = true;
      uniforms['tSpecular'].texture =
        THREE.ImageUtils.loadTexture('textures/' + planet.name + '_hydro.jpg');
      uniforms['uSpecularColor'].value.setHex(0xFFFFFF);
    }

    if (planet.texture_terrain) {
      uniforms['tNormal'].texture =
        THREE.ImageUtils.loadTexture('textures/' + planet.name + '_terrain.jpg');
      uniforms['uNormalScale'].value = 0.85;
    }

    planetMaterial = new THREE.MeshShaderMaterial({
        fragmentShader: shader.fragmentShader,
        vertexShader: shader.vertexShader,
        uniforms: uniforms,
        lights: true
      });
  }

  var surface = new THREE.Mesh(geometry, planetMaterial);
  globe.addChild(surface);
  globe.surface = surface;

  if (globe.planet.texture_atmosphere) {
    var atmosTexture = THREE.ImageUtils.loadTexture('textures/' + globe.planet.name + '_atmos.png');
    var materialAtmos =
      new THREE.MeshLambertMaterial({color: 0xffffff, map: atmosTexture, transparent: true});
    atmosphere = new THREE.Mesh(geometry, materialAtmos);
    //atmosphere.position.set(xOff, 0, 0);
    atmosphere.scale.set(atmosScale, atmosScale, atmosScale);
    atmosphere.rotation.z = globe.planet.axialInclination;
    globe.addChild(atmosphere);
    globe.atmosphere = atmosphere;
  }

  globe.rotation.z = globe.planet.axialInclination;

  return globe;
};

THREE.EllipseCurve = function(aX, aY, aRadius, eccentricity,
                             aStartAngle, aEndAngle,
                             aClockwise) {

  this.aX = aX;
  this.aY = aY;
  this.aRadius = aRadius;
  this.bRadius = aRadius * Math.sqrt(1.0 - Math.pow(eccentricity, 2.0));
  this.aStartAngle = aStartAngle;
  this.aEndAngle = aEndAngle;
  this.aClockwise = aClockwise;
};

THREE.EllipseCurve.prototype = new THREE.Curve();
THREE.EllipseCurve.prototype.constructor = THREE.EllipseCurve;
THREE.EllipseCurve.prototype.getPoint = function (t) {

  var deltaAngle = this.aEndAngle - this.aStartAngle;

  if (!this.aClockwise) {
    t = 1 - t;
  }

  var angle = this.aStartAngle + t * deltaAngle;

  var tx = this.aX + this.aRadius * Math.cos(angle);
  var ty = this.aY + this.bRadius * Math.sin(angle);

  return new THREE.Vector2(tx, ty);
};

// Move object to parent's semiMajorAxis; this only works for 1
// level nesting and x-axis offsets.
function displayOrbit(parentNode, orbit, parentSemiMajorAxis) {
  var orbitXoff = parentSemiMajorAxis * orbitScale;
  console.log('displayOrbit: parent: ' + parentNode.planet.name
              + ', orbit.semiMajorAxis: ' + orbit.semiMajorAxis
              + ', parentSemiMajorAxis; ' + parentSemiMajorAxis
              + ', orbit xOff: ' + orbitXoff);
  var ellipseCurve = new THREE.EllipseCurve(0, 0,
                                            orbit.semiMajorAxis * orbitScale,
                                            orbit.eccentricity,
                                            0, 2.0 * Math.PI,
                                            false);
  var ellipseCurvePath = new THREE.CurvePath();
  ellipseCurvePath.add(ellipseCurve);
  var ellipseGeometry = ellipseCurvePath.createPointsGeometry(100);
  ellipseGeometry.computeTangents();
  var orbitMaterial = new THREE.LineBasicMaterial({color: 0x0000ff});
  var line = new THREE.Line(ellipseGeometry, orbitMaterial);
  line.rotation.x = Math.PI / 2.0;
  if (parentNode) {
    parentNode.addChild(line);
  }
};

window.onload = function() {
  if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
    return;
  }
  init();
  animate();
};

function onWindowResize(event) {
  width = window.innerWidth;
  height = window.innerHeight;

  renderer.setSize(width, height);

  camera.aspect = width / height;
  camera.updateProjectionMatrix();

  camera.screen.width = width;
  camera.screen.height = height;

  camera.radius = (width + height) / 4;
};

function animate() {
  requestAnimationFrame(animate);
  render();
};

function render() {
  var t = new Date().getTime(),
    dt = (t - time) / timeScale;
  time = t;

  if (root) {
    renderSystem(root, dt);
  }

  if (renderer) {
    renderer.clear();
    renderer.render(scene, camera);
  }
};

function renderSystem(system, dt) {
  var rot = Math.PI * 2.0 * (dt / system.planet.siderealRotationPeriod);
  system.surface.rotation.y += rot;
  var pos;
  if (system.planet.orbit) {
    // TODO: switch this back to negative.
    var angle = Math.PI * 2.0 * (dt / system.planet.orbit.siderealOrbitPeriod);
    pos = new THREE.Vector3(Math.cos(angle) * system.position.x
                            - Math.sin(angle) * system.position.z,
                            0,
                            Math.sin(angle) * system.position.x
                            + Math.cos(angle) * system.position.z);
  } else {
    pos = new THREE.Vector3(0,0,0);
  }
  system.position = pos;
  if (system.atmosphere) {
    system.atmosphere.rotation.y += rot;
  }
  for (c in system.children) {
    var p = system.children[c];
    if (p.planet) {
      renderSystem(p, dt);
    }
  }
}