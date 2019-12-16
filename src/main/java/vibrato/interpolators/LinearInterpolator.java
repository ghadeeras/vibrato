package vibrato.interpolators;

import vibrato.functions.DiscreteRealFunction;

public class LinearInterpolator implements Interpolator {

    public LinearInterpolator() {
    }

    public double value(DiscreteRealFunction function, int index, double fraction) {
        double xn   = function.apply(index);
        double xnp1 = function.apply(index + 1);
        return (xnp1 - xn) * fraction + xn;
    }

}