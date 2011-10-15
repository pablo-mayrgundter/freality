function animateSystem(system, dt) {

  if (system.surface) {
    var rot = Math.PI * 2.0 * (dt / system.props.siderealRotationPeriod);
    system.surface.rotation.y += rot;

    var pos;
    if (system.props.orbit) {
      var angle = Math.PI * 2.0 * (dt / system.props.orbit.siderealOrbitPeriod);
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

  for (ndx in system.children) {
    var child = system.children[ndx];
    if (child.props) {
      animateSystem(child, dt);
    }
  }
}
