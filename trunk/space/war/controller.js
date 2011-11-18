angular.service('Object', function($resource) {
    return $resource('data/:name.json');
  });

function ObjectCtrl(Object) {

  this.object = {};

  this.sceneNodes = [];

  this.load = function(objectName) {
    if (!objectName) {
      console.log('ObjectCtrl: load: ERROR, missing object name.');
      return;
    }
    console.log('Loading: ' + objectName);
    objectName = objectName.toLowerCase().replace(/ */g, '');
    //if (this.sceneNodes[objectName]) {
    //  this.select(this.sceneNodes[objectName]);
    //  return;
    //}
    var me = this;
    return Object.get({name:objectName},
                      function(obj) { me.display(obj); });
  };

  this.toggleOrbits = function() {
    console.log('toggleOrbits not implemented');
  };

  this.select = function(node) {
    console.log('Camera to: ' + node.props.name);
    targetNode = node;
  };

  this.display = function(props) {
    console.log('display: ' + props.name);
    this.object = props;

    var parentNode = this.sceneNodes[props.parent];
    if (!parentNode) {
      parentNode = scene;
    }

    var obj;
    if (props.type == 'galaxy') {
      obj = new THREE.Object3D; // to be a parent for stars.
      obj.orbitPosition = obj;
    } else if (props.type == 'stars') {
      obj = newStars(props, stars); // TODO(pablo): get rid of global for stars.
      camera.position.z = props.radius * radiusScale * 5;
    } else if (props.type == 'star') {
      //obj = newStar(props);
      obj = new THREE.Object3D;
      // TODO(pablo): add light at orbitPosition?
      scene.add(newPointLight());
    } else if (props.type == 'planet') {
      obj = newOrbitingPlanet(props);
      var orbt = newOrbit(props.orbit);
      if (parentNode.orbitPosition) {
        parentNode.orbitPosition.add(orbt);
      } else {
        parentNode.add(orbt);
      }
    }

    if (parentNode.orbitPosition) {
      parentNode.orbitPosition.add(obj);
    } else {
      parentNode.add(obj);
    }

    obj['props'] = props;
    this.sceneNodes[props.name] = obj;

    if (props.system) {
      for (s in props.system) {
        var name = props.system[s];
        var me = this;
        Object.get({name:name},
                      function(p){ me.display(p); });
      }
    }
  };

  this.$watch('name', 'load(name)');
}
