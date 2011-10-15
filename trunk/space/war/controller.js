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
    if (this.sceneNodes[objectName]) {
      this.select(this.sceneNodes[objectName]);
      return;
    }
    var me = this;
    return Object.get({name:objectName},
                      function(obj) { me.display(obj); });
  };

  this.toggleOrbits = function() {
    console.log('toggleOrbits not implemented');
  };

  this.select = function(node) {
    console.log('Camera to: ' + node.props.name);
    camera.position.x = node.position.x + 100;
    camera.position.y = node.position.y + 100;
    camera.position.z = node.position.z + 100;
  };

  this.display = function(props) {
    console.log('display: ' + props.name);
    this.object = props;

    // TODO(pablo): shouldn't need this.
    if (props.type != 'stars') {
      fixup(props);
    }

    var parentNode = this.sceneNodes[props.parent];
    if (!parentNode) {
      parentNode = scene;
    }
    var obj;
    if (props.type == 'galaxy') {
      obj = createStar(props, scene);
    } else if (props.type == 'stars') {
      obj = createStars(props);
    } else if (props.type == 'star') {
      obj = createStar(props, scene);
      // TODO(pablo): maybe specify camera setup in data configs?
      camera.position.z = props.radius * 7;
    } else if (props.type == 'planet') {
      obj = createPlanet(parentNode, props);
    }
    parentNode.addChild(obj);

    obj.props = props;
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

/**
 * Fix up wire notation.
 * TODO(pablo): shouldn't need to do this.
 */
function fixup(props) {
  props.radius = props.radius.match(/\d+(.\d+)?/)[0];
  props.siderealRotationPeriod = props.siderealRotationPeriod.match(/\d+(.\d+)?/)[0];
  props.axialInclination = props.axialInclination / 360.0 * 2.0 * Math.PI;
  if (props.orbit) {
    props.orbit.semiMajorAxis = parseFloat(props.orbit.semiMajorAxis);
    props.orbit.siderealOrbitPeriod = parseFloat(props.orbit.siderealOrbitPeriod);
  }
}
