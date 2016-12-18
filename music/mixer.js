const environment = flock.init();

const synth = flock.synth({
    synthDef: {
      id: "animator",
      ugen: "flock.ugen.triggerCallback",
      trigger: {
        ugen: "flock.ugen.impulse",
        freq: 10,
        phase: 0.5
      },
      options: {
        callback: {
          func: function () {
            box.animate();
          }
        }
      }
    }
  });

const band = flock.band({
    components: {
      sinSynth: {
        type: "flock.synth",
        options: {
          synthDef: {
            id: "carrier",
            ugen: "flock.ugen.sinOsc",
            freq: 440,
            mul: {
              ugen: "flock.ugen.line",
              start: 0,
              end: 0.25,
              duration: 1.0
            }
          }
        }
      },

      scheduler: {
        type: "flock.scheduler.async",
        options: {
          components: {
            synthContext: "{sinSynth}"
          },
          score: [{
              interval: "repeat",
              time: 1.0,
              change: {
                values: {
                  "carrier.freq": {
                    synthDef: {
                      ugen: "flock.ugen.sequence",
                      values: [440]
                    }
                  }
                }
              }
            }]
        }
      }
    }
  });

function pulse() {
  band.scheduler.once(0, function () {
      band.sinSynth.set({
          "carrier.mul.start": 0.25,
            "carrier.mul.end": 0.0,
            "carrier.mul.duration": 1.0
            });
    });
}

let envRunning = false;
function startSound() {
  if (!envRunning) {
    environment.start();
    envRunning = true;
  }
  pulse();
}
function stopSound() {
  environment.stop();
}

startSound();
