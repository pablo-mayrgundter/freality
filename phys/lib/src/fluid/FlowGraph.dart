import 'dart:html';

import 'package:phys/src/fluid/Color.dart';
import 'package:phys/src/fluid/HexGrid.dart';

/**
 * The FlowGraph class takes a canvas and a desired grid scale and internally
 * sets up a graphics object and params for how to draw to it at that size.  The
 * graph may be single pixel if the grid scale = 1 or may be a HexGrid with the
 * appropriate number of cells at the scale to fit the canvas size.
 *
 * The caller should construct a FlowGraph, then call prepDraw once, with a
 * function that will be called for each pixel to determine its palette value.
 * Subsequent to this, drawOnce should be called between grid updates to animate
 * the change on the canvas.
 */
class FlowGraph {

  int gridScale;
  var hexGrid;
  int radius;
  List<Color> palette = null;

  FlowGraph(var canvas, int this.gridScale) {
    var graphics = canvas.getContext('2d');
    if (gridScale == 1) {
      radius = canvas.width;
    } else {
      radius = (canvas.width / 4 / gridScale).toInt();
    }
    palette = new List<Color>();
    // TODO(pmy): this should be in Flow.dart b/c the ref to field bit size.
    // Only 6 bits can be set in the force field.
    for (int i = 0; i < 7; i++) {
      int val = (255.0 * (i.toDouble() / 6.0)).toInt();
      palette.add(new Color(0, val, val));
    }
    hexGrid = new HexGrid(radius, radius, gridScale, graphics);
  }

  var coordColorFn;

  void prepDraw(var f) {
    coordColorFn = f;
  }

  void drawOnce() {
    if (gridScale == 1) {
      ImageData img = hexGrid.getImage();
      var data = img.data;
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          Color pixColor = palette[coordColorFn(x, y)];
          int ndx = (y * radius + x) * 4;
          data[ndx] = pixColor.r;
          data[ndx + 1] = pixColor.g;
          data[ndx + 2] = pixColor.b;
          data[ndx + 3] = 255;
        }
      }
      hexGrid.g.putImageData(img, 0, 0);
    } else {
      for (int y = 0; y < radius; y++) {
        for (int x = 0; x < radius; x++) {
          hexGrid.next(palette[coordColorFn(x, y)]);
        }
        hexGrid.line();
      }
      hexGrid.reset();
    }
  }
}
