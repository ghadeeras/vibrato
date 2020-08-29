package vibrato.complex;

public class ComplexNumber implements Complex<ComplexNumber> {

    private double real;
    private double imaginary;

    public ComplexNumber() {
        this(0, 0);
    }

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public static ComplexNumber createRI(double real, double imaginary) {
        return new ComplexNumber(real, imaginary);
    }

    public static ComplexNumber createLA(double length, double angle) {
        var complexNumber = new ComplexNumber();
        return complexNumber.setLA(length, angle);
    }

    @Override
    public double real() {
        return real;
    }

    @Override
    public double imaginary() {
        return imaginary;
    }

    @Override
    public ComplexNumber setRI(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
        return this;
    }

    private ComplexNumber copy() {
        return createRI(real, imaginary);
    }

    public ComplexNumber plus(Complex<?> c) {
        return copy().add(c);
    }

    public ComplexNumber minus(Complex<?> c) {
        return copy().sub(c);
    }

    public ComplexNumber negated() {
        return copy().negate();
    }

    public ComplexNumber conjugate() {
        return copy().doConjugate();
    }

    public ComplexNumber times(Complex<?> c) {
        return copy().mul(c);
    }

    public ComplexNumber scaled(double factor) {
        return copy().scale(factor);
    }

    public ComplexNumber rotated(double angle) {
        return copy().rotate(angle);
    }

    public ComplexNumber dividedBy(Complex<?> c) {
        return copy().div(c);
    }

    public ComplexNumber reciprocal() {
        return copy().reciprocate();
    }

    @Override
    public String toString() {
        return toRIString();
    }

}
