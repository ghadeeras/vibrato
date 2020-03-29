# Complex Numbers
The `vibrato.complex` package contains the classes that represent and manipulate complex numbers. Complex numbers are 
fundamental to DSP. For example they are used in pole-zero filter design and in implementing FFT.

Complex numbers are represented by the [`Complex`](Complex.java), which has two main implementations:

 * [`ComplexNumber`](ComplexNumber.java): Represents single complex numbers.
 * [`ComplexBuffer.Pointer`](ComplexBuffer.java): Represent an index-referenced complex number in a buffer of complex numbers.

Example:
```java
class MyClass {
    public static void rotateAndScale(ComplexNumber c, double angle, doube factor) {
        c.rotate(angle).scale(factor);
    }
}
```
