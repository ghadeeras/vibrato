package vibrato.interpolators;

import vibrato.functions.DiscreteRealFunction;
import vibrato.functions.RealFunction;

public class LinearInterpolator implements Interpolator {

    public LinearInterpolator() {
    }

    @Override
    public double value(DiscreteRealFunction function, int index, double fraction) {
        double xn   = function.apply(index);
        double xnp1 = function.apply(index + 1);
        return (xnp1 - xn) * fraction + xn;
    }

    @Override
    public RealFunction asFunction(DiscreteRealFunction function, int index) {
        double xn   = function.apply(index);
        double xnp1 = function.apply(index + 1);
        double tan  = xnp1 - xn;
        return fraction -> tan * fraction + xn;
    }

}