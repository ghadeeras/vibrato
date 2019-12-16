package vibrato.functions;

import vibrato.interpolators.Interpolator;

public interface DiscreteRealFunction {

    double apply(int i);

    default DiscreteRealFunction shiftOnXAxis(int shift) {
        return i -> apply(i - shift);
    }

    default DiscreteRealFunction shiftOnYAxis(double shift) {
        return i -> apply(i) + shift;
    }

    default DiscreteRealFunction flipAroundYAxis() {
        return i -> apply(-i);
    }

    default DiscreteRealFunction compressOnYAxis(double factor) {
        return i -> apply(i) / factor;
    }

    default DiscreteRealFunction stretchOnYAxis(double factor) {
        return i -> apply(i) * factor;
    }

    default DiscreteRealFunction flipAroundXAxis() {
        return i -> -apply(i);
    }

    default DiscreteRealFunction flip() {
        return i -> -apply(-i);
    }

    default DiscreteSignal asSignal() {
        return this::apply;
    }

    default RealFunction interpolated(Interpolator interpolator) {
        return x -> interpolator.value(this, x);
    }

}
