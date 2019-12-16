package vibrato.functions;

public interface RealFunction {

    double apply(double x);

    default RealFunction apply(RealFunction function) {
        return x -> apply(function.apply(x));
    }

    default Signal apply(Signal signal) {
        return t -> apply(signal.at(t));
    }

    default DiscreteRealFunction apply(DiscreteRealFunction function) {
        return x -> apply(function.apply(x));
    }

    default DiscreteSignal apply(DiscreteSignal signal) {
        return t -> apply(signal.at(t));
    }

    default RealFunction then(RealFunction f) {
        return f.apply(this);
    }

    default RealFunction shiftOnXAxis(double shift) {
        return x -> apply(x - shift);
    }

    default RealFunction shiftOnYAxis(double shift) {
        return x -> apply(x) + shift;
    }

    default RealFunction compressOnXAxis(double factor) {
        return x -> apply(x * factor);
    }

    default RealFunction stretchOnXAxis(double factor) {
        return x -> apply(x / factor);
    }

    default RealFunction flipAroundYAxis() {
        return x -> apply(-x);
    }

    default RealFunction compressOnYAxis(double factor) {
        return x -> apply(x) / factor;
    }

    default RealFunction stretchOnYAxis(double factor) {
        return x -> apply(x) * factor;
    }

    default RealFunction flipAroundXAxis() {
        return x -> -apply(x);
    }

    default RealFunction flip() {
        return x -> -apply(-x);
    }

    default DiscreteRealFunction discrete() {
        return this::apply;
    }

    default Signal asSignal() {
        return this::apply;
    }

}
