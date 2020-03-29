package vibrato.complex;

/**
 * Implementors of this interface are representations of complex numbers and the main operations that apply to them.
 * <p/>
 * In order to avoid the performance cost of instantiating and garbage-collecting complex numbers, a mutable interface
 * is favoured. The design assumes that all the needed instances of complex numbers are created at construction-time
 * only, and that its functions operate on these instances by mutating them, not by creating new instances, making them
 * efficient at processing-time.
 * <p/>
 * The generic type is only meant to allow the return types of the operations to be of the same type as the classes that
 * implement this class, instead of being of the abstract interface type. Say you want to create your own implementation
 * (MyComplexNumber) you would define t as follows:
 * <pre>{@code
 *  class MyComplexNumber implements Complex<MyComplexNumber> {
 *      ...
 *      double myOwnOperation(): double
 *  }
 * }</pre>
 * This allows you to chain method calls without losing access to specialized operations that you add in your
 * implementation:
 * <pre>{@code
 *  MyComplexNumber c = ...
 *  double result = c
 *      .rotate(Math.Pi / 6)
 *      .scale(2)
 *      .myOwnOperation(); // This would not work if rotate() and scale() returned just a Complex.
 * }</pre>
 * <b>Important Note</b>: All operations that return a complex number in this interface are expected to be identity
 * functions, returning the same instance that is operated on.
 * @param <C> the implementor type
 */
public interface Complex<C extends Complex<?>> {

    double real();

    double imaginary();

    default double length() {
        return Math.sqrt(lengthSquared());
    }

    default double angle() {
        return Math.atan2(imaginary(), real());
    }

    default double lengthSquared() {
        var r = real();
        var i = imaginary();
        return r * r + i * i;
    }

    C setRI(double real, double imaginary);

    default C setLA(double length, double angle) {
        return setRI(length * Math.cos(angle), length * Math.sin(angle));
    }

    default C set(Complex<?> c) {
        return setRI(c.real(), c.imaginary());
    }

    default C add(Complex<?> c) {
        return setRI(real() + c.real(), imaginary() + c.imaginary());
    }

    default C sub(Complex<?> c) {
        return setRI(real() - c.real(), imaginary() -  c.imaginary());
    }

    default C negate() {
        return setRI(-real(), -imaginary());
    }

    default C doConjugate() {
        return setRI(real(), -imaginary());
    }

    default C mul(Complex<?> c) {
        var x2 = c.real();
        var y2 = c.imaginary();
        return mul(x2, y2);
    }

    default C mul(double real, double imaginary) {
        var r = real();
        var i = imaginary();
        return setRI(r * real - i * imaginary, r * imaginary + i * real);
    }

    default C scale(double factor) {
        return setRI(factor * real(), factor * imaginary());
    }

    default C rotate(double angle) {
        return mul(Math.cos(angle), Math.sin(angle));
    }

    default C div(Complex<?> c) {
        return setLA(length() / c.length(), angle() - c.angle());
    }

    default C reciprocate() {
        return setLA(1 / length(), -angle());
    }

    default String toRIString() {
        return real() + " + " + imaginary() + " i";
    }

    default String toLAString() {
        return length() + " < " + angle() / Math.PI + " Pi";
    }

}
