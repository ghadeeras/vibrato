package vibrato.interpolators;

import vibrato.functions.DiscreteRealFunction;

public class CubicInterpolator implements Interpolator {

    public CubicInterpolator() {
    }

    public double value(DiscreteRealFunction function, int index, double fraction) {
        double xnm1 = function.apply(index - 1);
        double xn = function.apply(index);
        double xnp1 = function.apply(index + 1);
        double xnp2 = function.apply(index + 2);
        double a = 0.5 * xnp2 - 1.5 * xnp1 + 1.5 * xn - 0.5 * xnm1;
        double b = -0.5 * xnp2 + 2.0 * xnp1 - 2.5 * xn + 1.0 * xnm1;
        double c = 0.5 * xnp1 - 0.5 * xnm1;
        return ((a * fraction + b) * fraction + c) * fraction + xn;
    }

}