package vibrato.complex;

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
