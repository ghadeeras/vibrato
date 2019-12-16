package vibrato.functions;

public interface RealFunction {

    double apply(double x);

    default RealFunction then(RealFunction f) {
        return x -> f.apply(apply(x));
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

    default Signal asSignal() {
        return this::apply;
    }

}
