package vibrato.interpolators;

import vibrato.functions.DiscreteRealFunction;
import vibrato.vectors.CircularBuffer;

public interface Interpolator {

    double value(DiscreteRealFunction function, int index, double fraction);

    default double value(DiscreteRealFunction function, double index) {
        double i = Math.floor(index);
        double fraction = index - i;
        return fraction != 0 ? value(function, (int) i, fraction) : function.apply((int) i);
    }

    default double[] resample(double[] wave, int newSize) {
        double ratio = wave.length / (double) newSize;
        double[] newWave = new double[newSize];
        CircularBuffer buffer = new CircularBuffer(wave);
        for (int i = 0; i < newSize; i++) {
            newWave[i] = value(buffer, i * ratio);
        }
        return newWave;
    }

    Interpolator truncating = (function, index, fraction) -> function.apply(index);
    Interpolator rounding = (function, index, fraction) -> function.apply(index + (int) Math.round(fraction));
    Interpolator linear = new LinearInterpolator();
    Interpolator cubic = new CubicInterpolator();

}
