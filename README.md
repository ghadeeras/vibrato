# Vibrato
![ci-build](https://github.com/ghadeeras/vibrato/workflows/ci-build/badge.svg)

Vibrato is a technique that musicians use to produce beautiful sounds from their instruments :-) ... However, in the 
context of this project, Vibrato is an awesome (or soon to be so) digital signal processing framework for the Java 
programing language.

It is still in a very early stage of the development process. So, bear with me please :-)

## Installation
Currently, there are no binary distributions for this framework. So, in order to use it, you would need to download the 
source code and build it on your own platform. 

### Prerequisites
 * **The Git version control system (version 2.27.0)**: This is the preferable way to download the source code, but
   alternatively, it could be downloaded from GitHub in a ZIP file.
 * **Java Development Kit (JDK version 11+)**: When running included scripts, you should have the `JAVA_HOME` 
   environment variable pointing to the installation path of your JDK.

### Steps
 * **Download the code**: If you have Git installed, and its binaries are in the search `PATH`, then from a terminal, 
   run the following command:
   
```shell
$ git clone https://github.com/ghadeeras/vibrato.git your/path/of/choice/to/vibrato
```

Alternatively, you could download the source packed in a ZIP file from:

```
https://github.com/ghadeeras/vibrato/archive/master.zip
```

Then unpack it into `your/path/of/choice/to/vibrato`
 * **Build the binaries**: To do so, run the following commands:

```shell
$ cd your/path/of/choice/to/vibrato
$ set JAVA_HOME="path/to/jdk11/installation" # if not already set
$ chmod +x gradlew # if on a Linux machine
$ ./gradlew build
```

Upon successful execution of previous commands the `vibrato` library will be built in a JAR file inside the `build/libs`
directory.

### Running Included Examples
There are a few examples included in the source code, under the test root directory (see the examples section below). If 
you successfully built the framework as explained in the steps above, you could run the examples to verify the
installation:

```shell
$ cd your/path/of/choice/to/vibrato # if not there already
$ set JAVA_HOME="path/to/jdk11/installation" # if not already set
$ ./gradlew collectLibraries # to copy some dependencies alongside the `vibrato` JAR.
$ chmod +x example # if on a Linux machine
$ example Doppler
```

The Doppler example (you could choose other examples as well) will play audio that sounds like a noisy source moving
repeatedly towards and away from the listener.

## Introductory Tutorial
In this brief tutorial, we introduce you to the basic concepts of a DSP system as built in Vibrato through a code 
walk-through of the `NoisyFifth` example available in the source code (under the tests root directory).

### The Objective
Let's say we want to build a system in which we: generate white noise (random signal), filter it through two, parallel, 
narrow-band filters, then output the two resulting signals to a stereo audio output device after applying some 
panning. The two filters will have their pass bands centered around the frequencies 293.66 Hz and 440.00 Hz.

**A Side-Note**: These frequencies happen to correspond to the middle D, and the middle A musical notes. Musicians refer 
to the "distance" between such notes as a "fifth" (since that distance comprises 5 notes: D, E, F, G, and A). Hence, the 
name of this example: `NoisyFifth`.

### The Building Blocks
The building blocks (or DSP units), as inferred from the objective above, are: noise source, two band-pass filters, a 
mixer (for panning), and an audio output device (or an audio sink).

#### The DSP System 
Imagine that the above-mentioned units are some electronic components that you need to assemble on an electronic board. 
The electronic board metaphor corresponds to the `DspSystem` class in Vibrato. So we need to extend the `DspSystem` 
class, and assemble all its units in its constructor:

```java
public class NoisyFifth extends DspSystem {

    /**
     * @param audioFormat The audio format is a standard Java Audio API object that will be needed when constructing the
     * audio sink (i.e. the line to the output audio device).
     */
    public NoisyFifth(AudioFormat audioFormat) {
        super(audioFormat.getFrameRate()); // Here we declare the intended sampling frequency.
        // Assemble all DSP units here.
    }
    
}
```

One of the features that the `DspSystem` class affords is a DSL for connecting the various DSP units in a declarative
way, as we will see.

#### The Noise Source
To declare a source, we use the `from()` method, which wires the source to the `DspSystem` and provides a starting 
point to build processing lines:

```java
    var randomSource = from(RandomSource.uniform(sameSeed()));
```

#### The Band-Pass Filters
We need two filters around two frequencies. So let's define a method that abstracts away all the other parameters needed
in defining a band-pass filter:

```java
    private DspFilter<RealValue, RealValue> bpf(double frequency) {
        return SecondOrderFilter.bpf(128, frequency, frequency / 440, 0.5);
    }
```

The two generic type parameters of `DspFilter` above indicate that this filter accepts a single real (scalar) value as 
input, and produces a single real value as output. In general, different filters could accept/produce other types, such
as vectors. The arguments that we passed to the BPF are: gain, middle frequency, bandwidth, and cutoff gain.

#### The Panning Mixer
The panning mixer will mix the outputs of the two filters (i.e. a vector of two components) using a matrix of 
coefficients, to produce another vector of two components representing the two stereo channels of the final output:

```java
    var panning = Mixer.matrix(new double[][]{
        {0.75, 0.25},
        {0.25, 0.75}
    });
```

The specified matrix has a row-major format to simplify the dot-product operations that the mixer performs.

#### The Audio Sink
The audio sink could be defined as follows:

```java
    var audioSink = AudioSink.create(audioFormat);
```

### The Wiring

We use the DSL methods that the `DspSystem` provides to wire all the components:

```java
    join(
        randomSource.through(bpf(293.66 * zHertz)),
        randomSource.through(bpf(440.00 * zHertz))
    ).through(panning).into(audioSink);
```

A few points to note in the above few lines:
 * The `through()` method (defined on sources already wired by the `from()` method) allows specifying a cascade 
   configuration.
 * The `join()` method allows creating multi-channel lines from separate single-channel lines. This is how we get a 
   stereo line, from two mono ones.
 * The `into()` method closes the circuit by flowing the signal into a sink.
 * The `zHertz` constant is calculated by the `DspSystem` (from the specified sampling frequency). It is often used 
   where methods/constructors expect frequency arguments.

### Powering Up the DSP System    
The only remaining work is to bring the DSP system to life by connecting it to an oscillator. Using the same electronic
circuit metaphor, the oscillator is similar to a Quartz clock that keeps the operations of a processor in sync:

```java
    public static void main(String[] args) {
        var audioFormat = new AudioFormat(44100, 16, 2, true, false);
        var system = new NoisyFifth(audioFormat);
        
        // Connect to an oscillator
        var oscillator = MainOscillator.create();
        system.connectTo(oscillator);
        
        // Run the oscillator
        oscillator.oscillateUntil(DspApp::pressedEnter);
    }
```

Connecting to an oscillator is usually the last step in creating a DSP application. That is because a DSP System also
happens to be an abstraction tool. The DSP system is itself a DSP unit that could be part of a bigger DSP 
system.

### Conclusion
 * DSP Systems are built in a declarative way as a subclass of the `DspSystem` class.
 * Various kinds of DSP units could exist in a DSP System, such as: sources, filters and sinks.
 * The connections between DSP units are specified using the `DspSystem` DSL.
 * An Oscillator is what actually kicks off a DSP system into action.
 * The complete example can be found [here](src/test/java/vibrato/examples/NoisyFifth.java).

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
[`DspSystem`](src/main/java/vibrato/dspunits/DspSystem.java) which provides a simple domain-specific language to connect 
DSP units in any desired configuration.

### Abstraction/Re-usability
This is closely related to Compose-ability and refers to the ability to re-use a "composite" unit or sub-system in 
other even higher level systems. This is doable because `CompositeUnit` and its subclass `DspSystem` themselves 
implement `DspUnit`.

An example of this is the [`BasicInstrument`](src/main/java/vibrato/music/synthesis/base/BasicInstrument.java)
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
