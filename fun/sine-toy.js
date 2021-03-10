import * as THREE from '/lib/three-68.min.js';
import * as hash from '../net/web/hashparams.js';

let uniforms = hash.getHashParams();

// http://localhost:8090/fun/flame.html#json=%7B%22time%22%3A%7B%22type%22%3A%22f%22%2C%22value%22%3A55.888%7D%2C%22rez%22%3A%7B%22type%22%3A%22v2%22%2C%22value%22%3A%7B%22x%22%3A1440%2C%22y%22%3A513%7D%7D%2C%22mouse%22%3A%7B%22type%22%3A%22v2%22%2C%22value%22%3A%7B%22x%22%3A686%2C%22y%22%3A1%7D%7D%2C%22zoom%22%3A%7B%22type%22%3A%22f%22%2C%22value%22%3A2.6920000000000064%7D%2C%22iter%22%3A%7B%22type%22%3A%22i%22%2C%22value%22%3A6%7D%7D
if (uniforms && uniforms.json) {
  uniforms = uniforms.json;
} else {
  uniforms = {
    time: {
      type: "f",
      value: 1.0
    },
    rez: {
      type: "v2",
      value: new THREE.Vector2()
    },
    mouse: {
      type: "v2",
      value: new THREE.Vector2()
    },
    zoom: {
      type: "f",
      value: 1.0
    },
    iter: {
      type: "i",
      value: 1
    }
  };
}

document.onmousemove = (e) => {
  e = e || window.event;
  uniforms.mouse.value.x = e.clientX;
  uniforms.mouse.value.y = e.clientY;
  hash.setHashParams(uniforms);
};

document.onwheel = (e) => {
  uniforms.zoom.value += 0.001 * e.deltaY;
  uniforms.zoom.value.toFixed(3);
  hash.setHashParams(uniforms);
};

document.onkeypress = (e) => {
  switch (e.which) {
  // '['
  case 91: uniforms.iter.value = uniforms.iter.value < 1 ? 0 : uniforms.iter.value - 1; break;
  // ']'
  case 93: uniforms.iter.value++; break;
  }
  hash.setHashParams(uniforms);
};

function makeScene(flameObject) {
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);
  camera.position.x = 0;
  camera.position.y = 0;
  camera.position.z = 970;
  const webgl = new THREE.WebGLRenderer();
  webgl.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(webgl.domElement);

  function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    webgl.setSize(window.innerWidth, window.innerHeight);
  }
  window.addEventListener('resize', onWindowResize, false);
  const light = new THREE.HemisphereLight(0xeeeeee, 0x888888, 1);
  light.position.set(0, 20, 0);
  scene.add(light);

  function render() {
    flameObject.update();
    webgl.render(scene, camera);
    requestAnimationFrame(render);
    console.log('.');
  }
  scene.add(flameObject.obj);
  render();
}


function FlameObject() {
  this.geometry = new THREE.PlaneGeometry(window.innerWidth, window.innerHeight, 1);
  this.uniforms = uniforms;
  this.uniforms.rez.value.x = window.innerWidth;
  this.uniforms.rez.value.y = window.innerHeight;
  this.material = new THREE.ShaderMaterial({
      uniforms: uniforms,
      vertexShader: document.getElementById('main.vert').textContent,
      fragmentShader: document.getElementById('flame.frag').textContent
    });
  this.obj = new THREE.Mesh(this.geometry, this.material);
  this.startTime = Date.now();
}


FlameObject.prototype.update = function() {
  const elapsedMilliseconds = Date.now() - this.startTime;
  const elapsedSeconds = elapsedMilliseconds / 1000.;
  this.uniforms.time.value = elapsedSeconds;
  //this.uniforms.time.value.toFixed(3);
};


function init() {
  console.log('here!');
  makeScene(new FlameObject());
}

init();