'use strict';

/**
 * The Controller loads the scene.  The scene nodes are fetched from
 * the server, a mapping is created for navigation to current scene
 * node locations based on names, and information is displayed for
 * the selected node.
 */
var Controller = function() {

  this.sceneNodes = {};
  this.sceneLive = false;
  this.sunLoaded = false;

  this.load = function(systemNamesList){
    console.log('loading: ' + systemNamesList);
    var systemNames = systemNamesList.split(',');
    var systemName = systemNames.shift();
    if (this.sceneNodes[systemName]) {
      this.select(systemName);
      return;
    }
    // This fetches the first resource and invokes a callback to
    // display below.
    var me = this;
    new Resource(systemName).get(function(obj) {
        me.display(obj);
        if (systemNames.length > 0) {
          setTimeout('ctrl.load("'+ systemNames.join(',') +'")', 0);
        } else {
          // Have to delay to give a chance to the object to animate
          // and gain its position in the scene.
          setTimeout('ctrl.select("'+ systemName +'")', 100);
        }
    });
  };

  this.display = function(props) {

    // Find a parent or add directly to scene.  TODO(pablo): this is
    // ugly, since this is the only way the scene goes live.
    var parentNode = this.sceneNodes[props.parent];
    if (!parentNode) {
      console.log('Adding node to scene root: ' + props.name);
      parentNode = scene;
    }

    console.log('Adding ' + props.type + ': ' + props.name);
    var obj;
    if (props.type == 'galaxy') {
      // TODO(pablo): a nice galaxy.
      obj = new THREE.Object3D;
      obj.orbitPosition = obj;
    } else if (props.type == 'stars') {
      // TODO(pablo): get rid of global for stars.
      obj = newStars(props, stars);
    } else if (props.type == 'star') {
      obj = newStar(props);
      obj.add(newPointLight());
      camera.position.set(0, 0, props.radius * radiusScale * 1E1);
    } else if (props.type == 'planet') {
      obj = newOrbitingPlanet(props);
    }

    // Add to scene in reference frame of parent's orbit position,
    // i.e. moons orbit planets, so they have to be added to the
    // planet's orbital center.
    if (parentNode.orbitPosition) {
      parentNode.orbitPosition.add(obj);
    } else {
      // Should only happen for milkyway.
      console.log('Parent has no orbit position: ' + props.name);
      parentNode.add(obj);
    }

    // This must happen before sub-systems loaded.
    obj['props'] = props;
    this.sceneNodes[props.name] = obj;
  };

  this.select = function(name) {
    console.log('selecting: ' + name);
    var node = this.sceneNodes[name];
    if (!node) {
      console.log('No such object: ' + name);
      return;
    }
    // TODO(pablo): select is currently called during init, where
    // sceneNodes is not yet populated?
    if (node.orbitPosition)  {
      targetObj = node.orbitPosition;
      targetObjLoc.identity();
      var curObj = targetObj;
      var objs = [];
      while (curObj.parent != scene) {
        objs.push(curObj);
        curObj = curObj.parent;
      }
      for (var i = objs.length - 1; i >= 0; i--) {
        var o = objs[i];
        targetObjLoc.multiplySelf(o.matrix);
      }

      var tPos = targetObjLoc.getPosition();
      var tStepBack = tPos.clone();
      tStepBack.negate();
      // TODO(pablo): if the target is at the origin (i.e. the sun),
      // need some non-zero basis to use as a step-back.
      if (tStepBack.isZero()) {
        tStepBack.set(0,0,1);
      }
      tStepBack.setLength(node.props.radius * orbitScale * 10.0);
      tPos.addSelf(tStepBack);
      camera.position.set(tPos.x, tPos.y, tPos.z);
    }

    var path = '';
    var cur = this.sceneNodes[node.props.parent];
    while (cur) {
      var curName = cur.props.name;
      if (curName == 'milky_way') {
        break;
      }
      path = '<a href="#' + curName + '" onclick="ctrl.load(\''+ curName +'\')">' + curName + '</a> &gt; ' + path;
      var next = this.sceneNodes[cur.props.parent];
      if (next == cur) {
        break;
      }
      cur = next;
    }
    var html = path + name + ' <ul>\n';
    html += this.showInfoRecursive(node.props, false, false);
    html += '</ul>\n';
    var infoElt = document.getElementById('info');
    infoElt.innerHTML = html;
    // collapsor.js
    makeTagsCollapsable(infoElt);
  };

  /**
   * Tries to select the sun as the active scene target.  This is
   * required because there are two async conditions that must be met
   * for the sun to be ready to show: 1) the sun needs to have been
   * loaded from the server and added to the scene graph (via
   * display()), 2) the milkyway node also needs to be loaded, and it
   * is the scene graph root, so attaching it to the scene makes the
   * sun visible.
   */
  this.trySelectSun = function() {
    if (this.sceneLive && this.sunLoaded) {
      this.select('sun');
    }
  };

  this.showInfoRecursive = function(obj, isArray, isSystem) {
    var html = '';
    for (var prop in obj) {
      if (obj.hasOwnProperty(prop)) {
        var val = obj[prop];
        html += '<li>';
        if (!isArray) {
          html += prop + ': ';
        }
        if (val instanceof Array) {
          if (prop == 'system') {
            html += '<ol>\n';
          } else {
            html += '<ol class="collapsed">\n';
          }
          html += this.showInfoRecursive(val, true, prop == 'system');
          html += '</ol>\n';
        } else if (val instanceof Object) {
          html += '<ul class="collapsed">\n';
          html += this.showInfoRecursive(val, false, false);
          html += '</ul>\n';
        } else {
          if (isSystem) {
            html += '<a href="#' + val + '" onclick="ctrl.load(this.innerHTML)">';
          }
          html += val;
          if (isSystem) {
            html += '</a>';
          }
        }
        html += '</li>\n';
      }
    }
    return html;
  };
};
