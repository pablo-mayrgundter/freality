var environment = flock.init();
// Periodically trigger a function that causes the scope area to shake.
var synth = flock.synth({
  synthDef: {
    ugen: 'flock.ugen.out',
    sources: [{
        id: 'tone1',
        ugen: 'flock.ugen.sinOsc'
      }, {
        id: 'tone2',
        ugen: 'flock.ugen.sinOsc'
      }
    ]
  }
});
var soundRunning = false;
function startSound() {
  soundRunning = true;
  environment.start();
}
function stopSound() {
  environment.stop();
  soundRunning = false;
}

