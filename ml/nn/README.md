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
