# Vibrato
![ci-build](https://github.com/ghadeeras/vibrato/workflows/ci-build/badge.svg)

Vibrato is a technique that musicians (specially of string instruments) use, to produce beautiful sounds from their 
instruments :-) ... However, in the context of this project, Vibrato is an awesome (or soon to be so) digital signal 
processing framework for the Java programing language.

It is still in a very early stage of the development process. So, bear with me please :-)

## Design Considerations
### Performance
One of the design objectives of this framework is to allow creating simple (but not too simple) audio processors that 
could work in real-time. This could be challenging in a language like Java (or in any language that allocates 
non-primitive data types in the heap rather than passing them on the stack, and that have garbage collectors that cannot 
be predictably controlled).

Given that instantiating and garbage-collecting is deemed detrimental to performance, in this documentation I will often
refer to two terms that influenced the design of this framework:

 * **Construction-time**: The time during which all processing units and any data structures they need get constructed.
 * **Processing-time**: The time during which samples from signal sources get consumed and processed.
 
Much like developers often refer to compile-time, and run-time, in this documentation I will make the distinction 
between construction-time, and processing-time. Both take place during runtime, but the former represents the 
bootstrapping of the signal processor, while the latter represents the time of actual processing of signals.     

The rule of thumb that drove some aspects of the design, and the implementation, is that any object instantiation should
take place during construction-time only. Processing-time should not cause the creation of any new instances.

### Compose-ability
This means the ability to compose signal processing systems from fundamental units in a declarative way. The basic 
building blocks are the atomic implementations of [`DspUnit`](src/main/java/vibrato/dspunits/DspUnit.java). Higher level
systems would extend [`CompositeUnit`](src/main/java/vibrato/dspunits/CompositeUnit.java) or the richer 
[`DspSystem`](src/main/java/vibrato/dspunits/DspSystem.java) which provide a simple domain-specific language to connect 
DSP units in any desired configuration.

### Abstraction/Re-usability
This is closely related to Compose-ability and refers to the ability to re-use a "composite" unit or sub-system in 
other even higher level systems. This is doable because `CompositeUnit` and its subclass `DspSystem` themselves 
implement `DspUnit`.

An example on this is the [`BasicInstrument`](src/main/java/vibrato/music/synthesis/base/BasicInstrument.java)
which is composed of wave oscillators, generators, and other basic operators.

### Multiple-Sampling Rate Support
If a DSP system involves up-sampling or down-sampling, or if it is a music synthesis system involving control signals 
which typically are sampled at much lower rate than audio signals, then the units of the system may be operating on 
different "clock speeds".

The ability to inter-connect and orchestrate the work of such units influenced the design. The main consequence of this
consideration is the two-phased implementation of stateful operations that DSP units use internally.

The two phases are:
 * **Reading phase**: this is the phase where stateful operations read input data (into private storage) that will be 
 used to update the state (say a circular buffer, or a delay line).
 * **Writing phase**: this is the phase where the state gets actually changed, which would also reflect as a change in 
 the output of DSP units.

So, DSP units, or their stateful operations, can be thought of as looking outwards during the reading phase, and looking 
inwards during the writing phase. This makes it possible to execute the operations in any order without worrying about
a DSP unit changing its output before a dependant unit getting the chance to read the previous output.

The reading phase and writing phase can also be thought of as the up-edge and down-edge of a clock signal or pulse. The
unit which provides these pulses is the [`Oscillator`](src/main/java/vibrato/oscillators/Oscillator.java). There are two
kinds of oscillators:
 * [`MainOscillator`](src/main/java/vibrato/oscillators/MainOscillator.java): Provides the highest clock rate 
 possible.
 * [`SubOscillator`](src/main/java/vibrato/oscillators/SubOscillator.java): Provides lower clock rates that are 
 rational fractions of the main oscillator rate. The main oscillator has factory methods for getting sub- oscillators. 
 It is also possible to have oscillators operating on the same speed, but on different phase shifts.
 
## Examples
To see how the framework can be used to construct simple audio applications, some samples can be found in the [test 
directory](src/test/java/vibrato/examples). To follow is a brief description of the examples:
 * [**C Major**](src/test/java/vibrato/examples/CMajor.java): An example that plays the C Major scale, with vibrato! :-)
 * [**C Major (Enveloped)**](src/test/java/vibrato/examples/CMajorEnveloped.java): Same as C Major but uses the 
 `BasicInstrument` class to apply an envelope on each note.
 * [**C Major FM (Enveloped)**](src/test/java/vibrato/examples/CMajorFM.java): Same as C Major but uses the 
 `BasicFMInstrument` class to use frequency modulation and apply an envelope on each note.
 * [**C Major (Subtractive)**](src/test/java/vibrato/examples/CMajorSubtractive.java): Same as C Major but applies a 
 dynamic comb filter on white noise to produce notes and their harmonics.
 * [**Mandlebrot**](src/test/java/vibrato/examples/MandelbrotPlayer.java): Playing a Mandelbrot chord :-).
 * [**Doppler**](src/test/java/vibrato/examples/Doppler.java): An example simulating Doppler effect. It simulates an 
 audio source that is moving around the listener.
 * [**Play To Oscilloscope**](src/test/java/vibrato/examples/CMajor.java): An example that plays a WAV file, performing
 FFT on it, and outputting the spectrum to an [`Oscilloscope`](src/main/java/vibrato/dspunits/sinks/Oscilloscope.java).

## Digging Deeper
To learn more, explore the various packages that comprise the project:
 * [`vibrato.complex`](src/main/java/vibrato/complex/ComplexNumbers.md)
 * [`vibrato.fourier`](src/main/java/vibrato/fourier/FourierTransforms.md)
 * [`vibrato.functions`](src/main/java/vibrato/functions/Functions.md)
