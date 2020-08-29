# Complex Numbers
The `vibrato.complex` package contains the classes that represent and manipulate complex numbers. Complex numbers are 
fundamental to DSP. For example, they are used in pole-zero filter design and in implementing FFT.

## The `Complex` Interface
The aforementioned classes implement the [`Complex`](Complex.java) interface.

### Mutability
Though the interface does not mandate mutability, the operations of the existing implementing classes mutate the complex 
numbers they operate on instead of returning new instances. The choice of mutability is to avoid the performance cost of 
instantiating and garbage-collecting complex numbers. This allows creating all the needed instances of complex numbers 
at construction-time only, and later use their functions to operate on these instances by mutating them, not by creating 
new instances, making them efficient at processing-time.

### The Generic Type C
The generic type C in the interface is only to allow the return types of the operations to be of the same type as the 
implementing classes, instead of being of the abstract interface type. Say you want to create your own implementation 
(MyComplexNumber) that has additional unique operations. You would define it as follows:

```java
class MyComplexNumber implements Complex<MyComplexNumber> { // <-- The same class is passed as generic type parameter
    ...
    double myOwnOperation() {
        ... 
    }
}
```

This allows you to chain method calls without losing access to the unique operations that you added in your
implementation:

```java
    MyComplexNumber c = ...
    double result = c
        .rotate(Math.Pi / 6)
        .scale(2)
        .myOwnOperation(); // <-- This would not compile if rotate() and scale() returned just a Complex.
```

There are two main implementations for the `Complex` interface:

## `ComplexNumber`
See: [`ComplexNumber`](ComplexNumber.java)

This class is a generic implementation of the `Complex` interface that works with single complex numbers. In addition to 
the operations declared in the interface, this class defines additional operations that are functional/non-mutating. 
These additional operations should only be used in construction-time, not in processing-time because they
return new instances, and so could be detrimental to performance.

Example:
```java
class MyClass {
    
    // Mutating operations
    public static ComplexNumber rotateAndScale(ComplexNumber c, double angle, doube factor) {
        ComplexNumber result = c.rotate(angle).scale(factor);
        assert result == c;
        return result;
    }
    
    // Functional/non-mutating operations
    public static ComplexNumber rotatedAndScaled(ComplexNumber c, double angle, doube factor) {
        ComplexNumber result = c.rotated(angle).scaled(factor);
        assert result != c;
        return result;
    }

}
```

## `ComplexBuffer.Pointer`
See [`ComplexBuffer`](ComplexBuffer.java)

The `ComplexBuffer` class allows manipulating an array of complex numbers. Usually a process manipulates a limited 
number of complex numbers at a time. So, it could create a minimal number of `Pointer` objects into the complex buffer, 
and slides these pointers along the length of the buffer. These pointers will represent the complex numbers they point 
to.

Say you have to implement an operation that reverses the order of complex numbers in a buffer. You can do this as 
follows:

```java
class MyClass {

    public Runnable reverserOf(ComplexBuffer buffer) {
        // Construction-time
        ComplexNumber c = new ComplexNumber();
        ComplexBuffer.Pointer p1 = buffer.pointer();
        ComplexBuffer.Pointer p2 = buffer.pointer();
        return () -> {
            // Processing-time
            for (int i = 0; i < buffer.size() / 2; i++) {
                p1.slideTo(i);
                p2.slideTo(buffer.size() - 1 - i);
                c.set(p1);
                p1.set(p2);
                p2.set(c);
            }
        };
    }

}
```

Notice that every time the returned `Runnable` runs, no instantiation of new complex numbers or pointers happen as they 
were pre-instantiated when constructing the runnable.

*To verify:* This design choice helps in having a more efficient access to the underlying real parts, and the imaginary 
parts, as parallel low-level/linear/continuous vectors of real numbers. See the methods: `realParts()` and 
`imaginaryParts()`.