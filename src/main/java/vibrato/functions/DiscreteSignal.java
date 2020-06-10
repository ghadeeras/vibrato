package vibrato.functions;

import vibrato.interpolators.Interpolator;

public interface DiscreteSignal {

    double at(int n);

    default DiscreteSignal delay(int delay) {
        return n -> at(n - delay);
    }

    default DiscreteSignal bias(double bias) {
        return x -> at(x) + bias;
    }

    default DiscreteSignal reverse() {
        return n -> at(-n);
    }

    default DiscreteSignal suppress(double factor) {
        return n -> at(n) / factor;
    }

    default DiscreteSignal amplify(double factor) {
        return n -> at(n) * factor;
    }

    default DiscreteSignal invert() {
        return n -> -at(n);
    }

    default DiscreteSignal flip() {
        return n -> -at(-n);
    }

    default DiscreteSignal causal() {
        return n -> n >= 0 ? at(n) : 0;
    }

    default DiscreteSignal periodic(int period) {
        return n -> at(n % period);
    }

    default DiscreteRealFunction asFunction() {
        return this::at;
    }

    default Signal interpolated(Interpolator interpolator) {
        return t -> interpolator.value(asFunction(), t);
    }

    default double[] samples(int count, int from, int stride) {
        double[] samples = new double[count];
        int to = from + stride * count;
        int j = 0;
        for (int i = from; i < to; i += stride) {
            samples[j++] = at(i);
        }
        return samples;
    }

}
