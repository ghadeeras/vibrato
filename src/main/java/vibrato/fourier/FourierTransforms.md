# Fourier Transforms
The `vibrato.fourier` package contains the classes that transform a "windowed signal" from/to its discrete time-domain 
representation (real time samples) to/from its discrete frequency-domain representation (complex frequency samples).

There are two main classes in this package:

 * [`FFT`](FFT.java): Transforms a signal (supplied as a `RealVector` or `ComplexBuffer.Pointer`) from time-domain to 
 frequency-domain. It outputs the frequency samples into another `ComplexBuffer`.  
 * [`IFFT`](IFFT.java): Transforms a signal (supplied as a `ComplexBuffer.Pointer`) from frequency-domain to 
 time-domain. It outputs the time samples into another `ComplexBuffer`.
 
For both classes, the constructor takes the minimum number of time/frequency samples. The actual number of samples is 
the nearest higher power of two.     

Example:
```java
class MyClass {
    
    public static void main(String[] args) {
        var sawSignal = RealVector.window(1000, i -> (i % 100) / 100d);
        var fft = new FFT(500);

        var buffer = new ComplexBuffer(fft.frequencySamplesCount());
        ComplexBuffer.Pointer pointer = buffer.pointer();

        fft.transform(sawSignal, pointer);

        for (var i = 0; i < fft.frequencySamplesCount(); i++) {
            System.out.println(pointer.slideTo(i));
        }
    }
    
}
```
