package vibrato.functions;

import vibrato.interpolators.Interpolator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

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
        private final int size;
        private final int firstIndexInclusive;
        private final int lastIndexExclusive;

        private Window(DiscreteRealFunction function, int firstIndexInclusive, int lastIndexExclusive) {
            this.function = function;
            this.size = lastIndexExclusive - firstIndexInclusive;
            this.firstIndexInclusive = firstIndexInclusive;
            this.lastIndexExclusive = lastIndexExclusive;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double firstValue() {
            return function.apply(firstIndexInclusive);
        }

        @Override
        public double lastValue() {
            return function.apply(lastIndexExclusive);
        }

        @Override
        public double value(int index) {
            return 0 <= index && index < size ? function.apply(firstIndexInclusive + index) : 0;
        }

    }

}
