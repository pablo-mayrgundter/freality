var atmosScale = 1.005,
  maxOrbit = 5869660000.0, // in km, pluto orbit's semi-major axis
  orbitScale = 1.0,
  starScale = maxOrbit * 100000.0, // arbitrary big multiplier to push them far out
  timeScale = 1.0,
  height = window.innerHeight,
  width  = window.innerWidth,
  container,
  camera, scene, renderer,
  root, globe,
  time = new Date().getTime();
var sceneNodes = [];
var showOrbits = false;
var starImage, starGlowMaterial;

angular.service('Planet', function($resource) {
    return $resource('data/:name.json');
  });

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

  this.toggleOrbits = function() {
    console.log('toggleOrbits');
    showOrbits = !showOrbits;
  }

  this.select = function(node) {
    console.log('Camera to: ' + node.planet.name);
    camera.position.x = node.position.x + 100;
    camera.position.y = node.position.y + 100;
    camera.position.z = node.position.z + 100;
  };

  this.display = function(p) {
    this.planet = p;
    // Fix up wire notation.
    // TODO(pablo): shouldn't need to do this.
    p.radius = p.radius.match(/\d+(.\d+)?/)[0];
    p.siderealRotationPeriod = p.siderealRotationPeriod.match(/\d+(.\d+)?/)[0];
    p.axialInclination = p.axialInclination / 360.0 * 2.0 * Math.PI;

    var parentNode = sceneNodes[p.parent];
    var obj = createPlanet(parentNode, p);
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
      parentNode.addChild(createOrbit(parentNode, p.orbit, parentSemiMajorAxis));
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
      near: 50, // ?
      far: starScale * 100.0, // arbitrary big amount further out.
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

  var dirLight = new THREE.PointLight(0xFFFFFF);
  dirLight.position.z = 10;
  scene.addLight(dirLight);

  starImage = THREE.ImageUtils.loadTexture('textures/star_glow.png');
  starGlowMaterial =
    new THREE.ParticleBasicMaterial({ color: 0xffffff,
                                      size: 69424800,
                                      map: starImage,
                                      sizeAttenuation: true,
                                      blending: THREE.AdditiveBlending,
                                      transparent: true });

  starMiniMaterial =
    new THREE.ParticleBasicMaterial({ color: 0xffffff,
                                      size: 4,
                                      map: starImage,
                                      sizeAttenuation: false,
                                      blending: THREE.AdditiveBlending,
                                      transparent: true });

  scene.addChild(createStars());

  // stars
  window.addEventListener('resize', onWindowResize, false);
}

// TODO(pablo): load from dataset.
function createStars() {
  var vector, starsGeometry = new THREE.Geometry();
  for (var i = 0; i < 10000; i++) {
    vector = new THREE.Vector3(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
    vector.multiplyScalar(starScale);
    starsGeometry.vertices.push(new THREE.Vertex(vector));
  }

  // For the sun.
  starsGeometry.vertices.push(new THREE.Vertex(new THREE.Vector3()));

  var stars = new THREE.Object3D();

  var starPoints = new THREE.ParticleSystem(starsGeometry, starMiniMaterial);
  starPoints.sortParticles = true;
  stars.addChild(starPoints);

  var starGlows = new THREE.ParticleSystem(starsGeometry, starGlowMaterial);
  starGlows.sortParticles = true;
  stars.addChild(starGlows);

  return stars;
}

function createPlanet(parentNode, planet) {
  console.log('createPlanet: '+ planet.name);
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
  var sphereGeom = new THREE.SphereGeometry(planet.radius, 100, 50);
  sphereGeom.computeTangents();

  var planetMaterial;
  if (planet.type == 'star') {
    var sunImage = THREE.ImageUtils.loadTexture('textures/sun-white.jpg');
    var sunMaterial = new THREE.MeshBasicMaterial({color: 0xffffff, map: sunImage});
    //var sunPointMaterial =
    //  new THREE.LineBasicMaterial({ color: 0xffffff });
    var sunMesh = new THREE.Mesh(sphereGeom, sunMaterial);
    globe.addChild(sunMesh);
    globe.surface = sunMesh;
  } else if (!(planet.texture_hydrosphere || planet.texture_terrain)) {
    var planetTexture = THREE.ImageUtils.loadTexture('textures/' + planet.name + '.jpg');
    planetMaterial = new THREE.MeshPhongMaterial({color: 0xffffff, map: planetTexture});
    var surface = new THREE.Mesh(sphereGeom, planetMaterial);
    globe.surface = surface;
    globe.rotation.z = globe.planet.axialInclination;
    globe.addChild(surface);
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

    var surface = new THREE.Mesh(sphereGeom, planetMaterial);
    globe.addChild(surface);
    globe.surface = surface;
    globe.rotation.z = globe.planet.axialInclination;
  }

  if (globe.planet.texture_atmosphere) {
    var atmosTexture = THREE.ImageUtils.loadTexture('textures/' + globe.planet.name + '_atmos.png');
    var materialAtmos =
      new THREE.MeshLambertMaterial({color: 0xffffff, map: atmosTexture, transparent: true});
    atmosphere = new THREE.Mesh(sphereGeom, materialAtmos);
    //atmosphere.position.set(xOff, 0, 0);
    atmosphere.scale.set(atmosScale, atmosScale, atmosScale);
    atmosphere.rotation.z = globe.planet.axialInclination;
    globe.addChild(atmosphere);
    globe.atmosphere = atmosphere;
  }

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
function createOrbit(parentNode, orbit, parentSemiMajorAxis) {
  var orbitXoff = parentSemiMajorAxis * orbitScale;
  console.log('createOrbit: parent: ' + parentNode.planet.name
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
  //line.type = 'orbit';
  if (parentNode) { // TODO(pablo): needed?
    parentNode.addChild(line);
  }
  return line;
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
  // dt in seconds, since orbital and rotational data are as well.
  var t = new Date().getTime(),
    dt = (t - time) / 1000.0 * timeScale;
  time = t;

  if (root) {
    animateSystem(root, dt);
  }

  if (renderer) {
    renderer.clear();
    renderer.render(scene, camera);
  }
};

function animateSystem(system, dt) {
  var rot = Math.PI * 2.0 * (dt / system.planet.siderealRotationPeriod);
  if (system.surface) {
    system.surface.rotation.y += rot;
    var pos;
    if (system.planet.orbit) {
      // TODO: switch this back to negative.
      var angle = Math.PI * -2.0 * (dt / system.planet.orbit.siderealOrbitPeriod);
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
  }
  for (c in system.children) {
    var p = system.children[c];
    if (p.planet) {
      animateSystem(p, dt);
    }
  }
}