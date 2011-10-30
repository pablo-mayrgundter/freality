var
  atmosScale = 1.005,
  maxOrbit = 5869660000.0, // in km, pluto orbit's semi-major axis
  orbitScale = 1.0,
  starScale = maxOrbit * 1000.0; // arbitrary big multiplier to push them far out

var globe;
var starImage, starGlowMaterial;

/**
 * Creates a cube of 10k random stars around the origin.
 *
 * TODO(pablo): load from dataset.
 */
function createStars(stars) {
  var starImage = THREE.ImageUtils.loadTexture('textures/star_glow.png');
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

  var vector, starsGeometry = new THREE.Geometry();
  for (var i = 0; i < stars.count; i++) {
    vector = new THREE.Vector3(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
    vector.multiplyScalar(starScale);
    starsGeometry.vertices.push(new THREE.Vertex(vector));
  }

  // For the sun.
  starsGeometry.vertices.push(new THREE.Vertex(new THREE.Vector3()));

  var shape = new THREE.Object3D();

  var starPoints = new THREE.ParticleSystem(starsGeometry, starMiniMaterial);
  starPoints.sortParticles = true;
  shape.addChild(starPoints);

  var starGlows = new THREE.ParticleSystem(starsGeometry, starGlowMaterial);
  starGlows.sortParticles = true;
  shape.addChild(starGlows);

  return shape;
}

function createStar(star, scene) {
  console.log('createStar: '+ star.name);

  var sunLight = new THREE.PointLight(0xFFFFFF);
  scene.addLight(sunLight);

  var sphereGeom = new THREE.SphereGeometry(star.radius, 100, 50);
  sphereGeom.computeTangents();
  var sunImage = THREE.ImageUtils.loadTexture('textures/sun-white.jpg');
  var sunMaterial = new THREE.MeshBasicMaterial({color: 0xffffff, map: sunImage});
  var sunMesh = new THREE.Mesh(sphereGeom, sunMaterial);

  var shape = new THREE.Object3D;
  shape.surface = sunMesh; // ?
  shape.addChild(sunMesh);
  return shape;
}

function createPlanet(parentNode, planet) {
  console.log('createPlanet: '+ planet.name);

  var sphereGeom = new THREE.SphereGeometry(planet.radius, 100, 50);
  sphereGeom.computeTangents();

  var planetMaterial;
  var planetTexture = THREE.ImageUtils.loadTexture('textures/' + planet.name + '.jpg');
  if (!(planet.texture_hydrosphere || planet.texture_terrain)) {
    planetMaterial = new THREE.MeshPhongMaterial({color: 0xffffff, map: planetTexture});
  } else {
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

  var shape = new THREE.Object3D;

  shape.position.set(planet.orbit.semiMajorAxis * orbitScale, 0, 0);

  var surface = new THREE.Mesh(sphereGeom, planetMaterial);
  shape.rotation.z = planet.axialInclination;
  shape.surface = surface;
  //shape.addChild(surface);

  if (planet.texture_atmosphere) {
    var atmosTexture = THREE.ImageUtils.loadTexture('textures/' + planet.name + '_atmos.png');
    var materialAtmos =
      new THREE.MeshLambertMaterial({color: 0xffffff, map: atmosTexture, transparent: true});
    atmosphere = new THREE.Mesh(sphereGeom, materialAtmos);
    atmosphere.scale.set(atmosScale, atmosScale, atmosScale);
    atmosphere.rotation.z = planet.axialInclination;
    shape.atmosphere = atmosphere; // ?
    //shape.addChild(atmosphere);
  }

  parentNode.addChild(createOrbit(planet.orbit));

  return shape;
};

rads = 2.0 * Math.PI / 360.0;

function createOrbit(orbit) {
  var ellipseCurve = new THREE.EllipseCurve(0, 0,
                                            orbit.semiMajorAxis * orbitScale,
                                            orbit.eccentricity * 3.0,
                                            0, 2.0 * Math.PI,
                                            false);
  var ellipseCurvePath = new THREE.CurvePath();
  ellipseCurvePath.add(ellipseCurve);
  var ellipseGeometry = ellipseCurvePath.createPointsGeometry(100);
  ellipseGeometry.computeTangents();
  var orbitMaterial = new THREE.LineBasicMaterial({color: 0x0000ff});
  var line = new THREE.Line(ellipseGeometry, orbitMaterial);
  // orbit.longitudeOfPerihelion = 20.0;
  line.rotation.z = orbit.longitudeOfPerihelion * rads; // Add true anomaly here.
  // orbit.inclination = 45.0;
  line.rotation.x = orbit.inclination * rads + Math.PI / 2.0;
  // orbit.longitudeOfAscendingNode = 45.0;
  //line.rotation.y = orbit.longitudeOfAscendingNode * rads;
  return line;
};
