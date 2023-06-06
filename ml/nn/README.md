Here's a Neural Network implementation based on a course I took
in college.  Neural Networks are designed to mimic some of the
structural and functional features we see working in our own
brains.  This particular kind of Neural Network is a vast
simplification of our actual neural networks.  It's called a
"back-propagation" neural network.  Despite being very simple, it
can "learn" to identify complex relations in data that is used to
"train" it.  Later, based on that training, it can identify the
same complex relations in new data that it hasn't seen before, as
long as the new data is vaguely similar to the old data.</p>


```
> java -Dusage=true Backprop
java <properties> Backprop

Properties:
  -Dusage=(true|false)	Print this message and exit.
  -DnumInputs=8
  -DnumHidden=4
  -DnumOutputs=8
  -DtargetError=0.05
  -Drate=0.05
  -Dseed=42
  -Dclamp=0.05		Clamp distance for rounding to 0 or 1 in outputs
```

Here's an example run to learn the identity function:

Build an NeuralNetwork to learn the identity function.  The training
samples are 8 bit fields, each with 8 bits, with one bit turned
on.  So, given:

`1 0 0 0 0 0 0 0`

the NeuralNetwork must produce this on the outputs:

`1 0 0 0 0 0 0 0`

Given:

`0 1 0 0 0 0 0 0`

the NeuralNetwork must produce this on the outputs:

`0 1 0 0 0 0 0 0`

and so on.  This is exactly what is done below.  Because the squashing
function can't yield 0 or 1 as outputs exactly, 0 is represented by
values less than 0.05 and 1 is represented by 0.95.  This threshold is
controlled by `clamp`.

The program outputs the error rates for successive training runs.
This should decrease with each run.

```
> java Backprop
Initial network (random):
Network:
numInputs: 8
numHidden: 4
numOutputs: 8
Seed: 1686092860284
[-0.69947636, 0.12324955, -0.43461508, -0.021117596]
[0.6336453, -0.36706576, 0.2852819, 0.84707135]
[0.101901285, 0.42542127, 0.19357678, -0.75251853]
[-0.8677406, 0.00822022, -0.76260597, 0.7702765]
[0.79480124, 0.8606052, 0.90903896, -0.8259894]
[0.40051952, 0.9400987, 0.40110087, -0.889167]
[0.5463841, 0.15692972, -0.58850896, 0.9278508]
[-0.8771276, -0.18152083, -0.03384752, -0.43673357]
[-0.10513349, 0.32940307, 0.38180843, -0.9495665]
[0.09455572, -0.10967837, 0.6381752, -0.061796214]
[-0.24586907, -0.57620865, -0.90885997, 0.9762366]
[-0.11401704, 0.24959737, -0.6721822, -0.25364828]
[0.92502016, -0.36156705, -0.5756408, 0.82764524]
[-0.33432767, -0.9364229, 0.6405601, 0.5008395]
[-0.8408492, 0.19681376, -0.18055981, 0.5998864]
[-0.7800504, 0.020293055, -0.5665446, 0.8823872]

Initial prediction:
[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] -> [1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0]

numIterations: 1, error 1.9247888

Trained network in 34824 iterations with final error 0.0499988:

Network:
numInputs: 8
numHidden: 4
numOutputs: 8
Seed: 1686092860284
[-0.094887115, 7.0684986, 1.8825167, -4.127377]
[3.032061, -0.5355435, 7.1427894, -5.2367344]
[5.5014925, 0.2206244, -4.2622023, 6.9347434]
[5.1571455, 6.9013743, -3.8323932, -2.606011]
[7.4335775, -0.9728434, -1.1058115, -1.6751468]
[4.5045395, -2.8091736, 5.513747, 6.553678]
[-0.7602311, 7.13579, -1.4148824, 2.4641213]
[-1.4990977, 1.2396635, 6.8171916, 5.4130487]
[-7.781533, 4.6163483, 2.5438886, -8.547457]
[-0.82019556, -8.720808, 6.849239, -9.25225]
[0.607022, -7.11321, -10.871669, 6.3767133]
[-1.0546868, 4.694636, -8.805015, -6.978447]
[7.903002, -10.59899, -6.7329984, -5.174396]
[-1.7170192, -10.8791, 1.8149093, 3.6266067]
[-8.557939, 4.7438927, -8.209461, 2.6934886]
[-10.878683, -2.7388403, 4.9954157, 2.1551678]

Trained prediction:
[1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] -> [1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
Trained prediction:
[0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] -> [0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
Trained prediction:
[0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0] -> [0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0]
Trained prediction:
[0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0] -> [0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0]
Trained prediction:
[0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0] -> [0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0]
Trained prediction:
[0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0] -> [0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0]
Trained prediction:
[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0] -> [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0]
Trained prediction:
[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0] -> [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0]
```
