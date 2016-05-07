import 'dart:async';
import 'dart:html';
import 'package:phys/Phys.dart';

Flow flow;
var animTimer;
InputElement cellSizeInput, delayInput, wallLeftRInput;
HtmlElement infoElt;
var startButton;
int timeCount = 0;
DateTime animStartTime;

void animate(ignoredArg) {
  flow.run();
  timeCount++;
  info('time: $timeCount');
  if (false && (timeCount % 10 == 0)) {
    animTimer.cancel();
    animTimer = null;
    timeCount++;
    var now = new DateTime.now();
    var animDuration = now.difference(animStartTime);
    infoAppend('Duration: $animDuration');
  }
}

/** Starts (or restarts) animation timer with current delayInput value. */ 
void restartAnimation() {
  if (animTimer != null) {
    animTimer.cancel();
  }
  animStartTime = new DateTime.now();
  int delay = int.parse(delayInput.value);
  animTimer = new Timer.periodic(new Duration(milliseconds: delay), animate);
}

void startButtonOnClickHandler() {
  if (animTimer != null) {
    animTimer.cancel();
    animTimer = null;
  } else {
    restartAnimation();
  }
}

void wallLeftRInputOnChangeHandler() {
  double wallLeftR = double.parse(wallLeftRInput.value);
  flow.setWallLeftR(wallLeftR);
  flow.run();
}

void info(msg) {
  infoElt.text = msg;
}

void infoAppend(msg) {
  infoElt.text = infoElt.text + '\n' + msg;
}

void main() {
  wallLeftRInput = querySelector('#wallLeftR');
  wallLeftRInput.onChange.listen((event) => wallLeftRInputOnChangeHandler());
  cellSizeInput = querySelector('#cellSize');
  infoElt = querySelector('#info');

  CanvasElement canvas = querySelector('#canvas');
  flow = new Flow(
      canvas,
      int.parse(cellSizeInput.value));
  flow.run();

  startButton = querySelector('#startButton');
  delayInput = querySelector('#delayInput');
  delayInput.onChange.listen((event) => restartAnimation());
  startButton.onClick.listen((event) => startButtonOnClickHandler());
}
