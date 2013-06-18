var graph;
var x, y, z, serial;
var morphLevel = 0,
  morphSpeed = 0,
  maxMorphSpeed = 0.005,
  morphAccel = 0.0001;
var numNodes = 1000;

/**
 * A single step in an Iterated Function System with linear and
 * trigonometric functions.
 *
 * Each node's integer sequence serial number is also used as an
 * intrinsic parameter (intrinsic meaning it's completely determined
 * by the development of the system itself), which gives a behavior
 * that "jumps" sequential points from one side of the curve to the
 * other, which are then connected by edges and this provides the nice
 * woven surface.  Thanks to Won Chun for helping to figure out this
 * behavior!
 *
 * Finally, a morphLevel extrinsic parameter is used to mutate the
 * total system to provide the basis for mutating the system through a
 * large phase space for the pretty animation.
 */
function ifs() {
  x = Math.sin(y + z) * Math.sin(y + morphLevel);
  y = Math.cos(x - z) * Math.cos(x - morphLevel);
  z = serial + morphLevel;
  serial++;
}

/**
 * A visitor for each node in the graph, to compute and assign its new
 * position and color (which is used for its edges as well).
 */
function renderNode(n) {
  // Step the IFS forward one step.
  ifs();

  // Move the node to its new position
  n.x = x;
  n.y = y;

  // Color mapping.
  var r = Math.abs(n.x) * 256,
    g = Math.abs(n.y) * 256,
    b = Math.abs(n.x + n.y / 2.0) * 256;
  n.color = '#' + sigma.tools.rgbToHex(r, g, b);
}

/**
 * Update all nodes positions to reflect the new development of the
 * IFS's attratcor orbits and redraw the graph.
 */
function renderSystem() {
  x = y = z = serial = 0;
  // Warmup the IFS to find the attractor, otherwise you get outlying
  // initial vectors.
  for (var i = 0; i < 10; i++) {
    ifs();
  }  

  // Positions the nodes in the graph.
  graph.iterNodes(renderNode);

  // The 2 params tell sigma to use "direct" drawing, which gets rid
  // of flickering in the default approach.
  graph.draw(2, 2, 2, 2);

  // Prepare the system for its next morph step. This is what morphs
  // the system's overall shape.  Use an accelerating delta to slowly
  // start the system moving or slow it down again.
  if (morphSpeed < maxMorphSpeed && morphSpeed >= 0) {
    morphSpeed += morphAccel;
  }

  morphLevel += morphSpeed;
}

/**
 * Compute and draw a single frame, which represents a sequence of an
 * IFS attractor orbit.
 */
function frame() {
  renderSystem();
  // Stop rendering if we're at zero speed and currently calling
  // frames.
  if (morphSpeed > 0) {
    animId = window.requestAnimationFrame(frame);
  } else {
    animId = null;
  }
}

/**
 * Setup an animation start/stop callback for user clicks.
 */
var animId;
function anim() {
  if (animId) {
    // If we're already animating, flip the direction of acceleration
    // from speeding up to slowing down or vice-versa.
    morphAccel *= -1;

    // renderSystem only accelerates while morphSpeed is less than
    // maxMorphSpeed, so if we're in that case above, we need to tap
    // the speed down from the limit to engage the conditional again,
    // decelerating the system
    if (morphSpeed >= maxMorphSpeed) {
      morphSpeed = maxMorphSpeed + morphAccel;
    }
    // Same for if we're at the bottom end limit.
    if (morphSpeed <= 0) {
      morphSpeed = morphAccel;
    }
  } else {
    // Start the system forward (maybe again).
    morphAccel = Math.abs(morphAccel);
    morphSpeed = morphAccel;
    animId = window.requestAnimationFrame(frame);
  }
}

/**
 * Initialize the sigma graph library, construct a graph with numNodes
 * and perform the first frame rendering.
 */
function init() {
  graph = sigma.init(document.getElementById('body'));

  // Construct the graph.
  var lastNode = null;
  for (var i = 0; i < numNodes; i++) {
    graph.addNode(i, {id:i,x:0,y:0,size:3});
    if (lastNode) {
      graph.addEdge(i, lastNode, i, {size:3});
    }
    lastNode = i;
  }
  renderSystem();
}

// From https://developer.mozilla.org/en-US/docs/Web/API/window.requestAnimationFrame
(function() {
  var requestAnimationFrame = window.requestAnimationFrame || window.mozRequestAnimationFrame ||
    window.webkitRequestAnimationFrame || window.msRequestAnimationFrame;
  window.requestAnimationFrame = requestAnimationFrame;
})();

