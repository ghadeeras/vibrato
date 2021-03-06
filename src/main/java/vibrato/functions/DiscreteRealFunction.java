package vibrato.functions;

import vibrato.interpolators.Interpolator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public interface DiscreteRealFunction {

    double apply(int i);

    default DiscreteRealFunction then(RealFunction function) {
        return function.apply(this);
    }

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

    default RealValue valueAt(int i) {
        return () -> apply(i);
    }

    default RealVector window(int size) {
        return window(0, size);
    }

    default RealVector window(int firstIndexInclusive, int lastIndexExclusive) {
        if (firstIndexInclusive > lastIndexExclusive) {
            return window(lastIndexExclusive + 1, firstIndexInclusive + 1);
        }
        return new Window(this, firstIndexInclusive, lastIndexExclusive);
    }

    class Window implements RealVector {

        private final DiscreteRealFunction function;
        private final int firstIndexInclusive;
        private final int lastIndexExclusive;
        private final int size;

        private Window(DiscreteRealFunction function, int firstIndexInclusive, int lastIndexExclusive) {
            this.function = function;
            this.firstIndexInclusive = firstIndexInclusive;
            this.lastIndexExclusive = lastIndexExclusive;
            this.size = lastIndexExclusive - firstIndexInclusive;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double value(int index) {
            return firstIndexInclusive <= index && index < lastIndexExclusive ? function.apply(index) : 0;
        }

    }

}
