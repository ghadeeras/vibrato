package vibrato.complex;

public class ComplexNumber implements Complex<ComplexNumber> {

    private double x;
    private double y;

    public ComplexNumber() {
        this(0, 0);
    }

    public ComplexNumber(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static ComplexNumber createXY(double x, double y) {
        return new ComplexNumber(x, y);
    }

    public static ComplexNumber createLA(double l, double a) {
        ComplexNumber complexNumber = new ComplexNumber();
        return complexNumber.setLA(l, a);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public ComplexNumber setXY(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    private ComplexNumber copy() {
        return new ComplexNumber(x, y);
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
        return copy().setXY(x, -y);
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
        return toXYString();
    }

}
