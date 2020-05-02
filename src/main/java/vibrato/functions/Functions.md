# Functions
The `vibrato.functions` package contains the interfaces and classes that represent real functions, both continues 
(i.e. R -> R) and discrete (i.e. I -> R).

The two main classes in this package are:

 * [`RealFunction`](RealFunction.java): It basically is a functional interface that takes a `double` and returns a 
 `double`. It also has default implementation for various basic transformations and composition functions.
 * [`DiscreteRealFunction`](DiscreteRealFunction.java): It basically is a functional interface that takes an `int` and 
 returns a `double`. It also has default implementation for various basic transformations and composition functions.
 
There are two other similar interfaces within this package as well that could add *semantic* convenience:
 
 * [`Signal`](Signal.java): Much like `RealFunction`, but is meant to represent a signal (i.e. a function of time). It 
 also has similar transformations and composition functions but with names that are more meaningful for signals (e.g 
 `delay()` instead of `shiftOnXAxis()`). 
 * [`DiscreteSignal`](DiscreteSignal.java): Same as `Signal` but uses discrete (integer) time.
 
The two set of interfaces have methods to convert from one form to another (i.e. function <--> signal, and  
continuous <--> discrete).

In addition, there are convenience implementations for these interfaces, such as `Linear`, `Sinc` and `Sinusoid`. 
However, in general, these interfaces are mainly meant to be constructed as lambdas. Example: 
`RealFunction myFunction = x -> (x + 1) * (x - 1)` 

## Transformations and Composition Functions

As stated earlier, the functions and signals interfaces provide default implementations to some transformations and 
composition functions.

Transformations operate or apply on one function. Examples on transformations: 

```java
var myFunction = Sinc.sinc() // x -> sinc(x)
    .stretchOnXAxis(5) // x -> sinc(x / 5)
    .compressOnYAxis(3) // x -> sinc(x / 5) / 3 
    .flipAroundYAxis(); // x -> sinc(-x / 5) / 3
```

Composition functions operate on two or more functions to give new functions. Examples on composition functions:

```java
var myFunction = Sinc.sinc() // x -> sinc(x)
    .then(Sinusoid.sin()); // x -> sin(sinc(x))
var myFunction = Sinc.sinc() // x -> sinc(x)
    .apply(Sinusoid.sin()); // x -> sinc(sin(x))
```

## Operators

The [`Operator`](Operator.java) interface represents an operator on real numbers. They accept two `doubles` (i.e. left and right 
operands) and return a `double`. One could construct an operator as a lambda. Example: 
`Operator myOp = (a, b) -> (a - b) / (a + b)`

Operators could also be used to compose new functions out of other functions. Example:

```java
Operator myOp = (a, b) -> (a - b) / (a + b);
RealFunction myFunction = myOp.apply(Sinc.sinc(), Sinusoid.sin());
// Yields: x -> (sinc(x) - sin(x)) / (sinc(x) + sin(x))
```