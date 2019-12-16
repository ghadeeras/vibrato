package vibrato.interpolators;

import vibrato.functions.DiscreteRealFunction;

public interface Interpolator {

    double value(DiscreteRealFunction function, int index, double fraction);

    default double value(DiscreteRealFunction function, double index) {
        double i = Math.floor(index);
        double fraction = index - i;
        return fraction != 0 ? value(function, (int) i, fraction) : function.apply((int) i);
    }

    Interpolator linear = new LinearInterpolator();

    Interpolator cubic = new CubicInterpolator();

}
