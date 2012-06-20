function elt(id) {
  return document.getElementById(id);
}

function log(msg) {
  elt('log').innerHTML += msg + '\n';
}

function logTape(msg) {
  elt('tape').innerHTML += msg + '\n';
}

function run(programId) {

  // A few lines to make debugging output useful.
  var e = elt(programId);
  var txt = e.value;
  var prog = eval(txt);

  var state = 'start';
  var tapeNdx = 0;
  var tape = new Array(10);

  // Set tape vals to 0.
  for (var i = 0; i < tape.length; i++) {
    tape[i] = 0;
  }

  elt('log').innerHTML = '';
  elt('tape').innerHTML = '';

  logTape('START: '+ tape +'\n');
  while (true) {

    // Parse instructions.
    logTape('state('+ state +'): '+ tape);
    var inst = prog[state];
    if (inst == 'stop') {
      break;
    }
    if (!inst) {
      log('At state ' + state +
                  ', no instruction found' + inst);
    }
    var val = tape[tapeNdx];
    log('tape['+ tapeNdx +'] val: ' + val);

    var write = null;
    var shift = null;
    var newState = null;
    if (inst.write) {
      write = inst.write;
      if (inst.shift)
        shift = inst.shift;
      if (inst.newState)
        newState = inst.newState;
    } else if (inst.read) {
      var obj = inst.read;
      var op = obj[val];
      if (!op) {
        log('At state ' + state +
                    ', unhandled input: ' + val);
      }
      if (op.write)
        write = op.write;
      if (op.shift)
        shift = op.shift;
      if (op.newState)
        newState = op.newState;
    } else if (inst.newState) {
      newState = inst.newState;
    }

    // Apply instructions.
    if (write)
      tape[tapeNdx] = write;

    if (shift) {
      if (shift == 'left') {
        log('moving left');
        tapeNdx--;
      } else if (shift == 'right') {
        log('moving right');
        tapeNdx++;
      }
    }

    if (newState)
      state = newState;

    // Basic bounds checking.
    if (tapeNdx < 0) {
      log('underflow');
      break;
    }
    if (tapeNdx >= tape.length) {
      log('overflow');
      break;
    }
    logTape('          '+ tape +'\n');
  }
  log('done');
  logTape('\nFINAL: ' + tape);
  return false;
}