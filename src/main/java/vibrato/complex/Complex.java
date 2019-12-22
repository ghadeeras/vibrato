package vibrato.complex;

public interface Complex<C extends Complex<?>> {

    double x();

    double y();

    default double length() {
        double x = x();
        double y = y();
        return Math.sqrt(x * x + y * y);
    }

    default double angle() {
        return Math.atan2(y(), x());
    }

    C setXY(double x, double y);

    default C setLA(double l, double a) {
        return setXY(l * Math.cos(a), l * Math.sin(a));
    }

    default C set(Complex c) {
        return setXY(c.x(), c.y());
    }

    default C add(Complex c) {
        return setXY(x() + c.x(), y() + c.y());
    }

    default C sub(Complex c) {
        return setXY(x() - c.x(), y() -  c.y());
    }

    default C negate() {
        return setXY(-x(), -y());
    }

    default C mul(Complex c) {
        double x2 = c.x();
        double y2 = c.y();
        return mul(x2, y2);
    }

    default C mul(double x, double y) {
        double x1 = x();
        double y1 = y();
        return setXY(x1 * x - y1 * y, x1 * y + y1 * x);
    }

    default C scale(double factor) {
        return setXY(factor * x(), factor * y());
    }

    default C rotate(double angle) {
        return mul(Math.cos(angle), Math.sin(angle));
    }

    default C div(Complex c) {
        return setLA(length() / c.length(), angle() - c.angle());
    }

    default C reciprocate() {
        return setLA(1 / length(), -angle());
    }

    default String toXYString() {
        return x() + " + " + y() + " i";
    }

    default String toRAString() {
        return length() + " < " + angle() / Math.PI + " Pi";
    }

}
