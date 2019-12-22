package vibrato.functions;

public interface Operator {

    double apply(double value1, double value2);

    default RealFunction apply(RealFunction function, RealFunction... functions) {
        return x -> {
            double result = function.apply(x);
            for (RealFunction f : functions) {
                result = apply(result, f.apply(x));
            }
            return result;
        };
    }

    default DiscreteRealFunction apply(DiscreteRealFunction function, DiscreteRealFunction... functions) {
        return i -> {
            double result = function.apply(i);
            for (DiscreteRealFunction f : functions) {
                result = apply(result, f.apply(i));
            }
            return result;
        };
    }

    default Signal apply(Signal signal, Signal... signals) {
        return t -> {
            double result = signal.at(t);
            for (Signal s : signals) {
                result = apply(result, s.at(t));
            }
            return result;
        };
    }

    default DiscreteSignal apply(DiscreteSignal signal, DiscreteSignal... signals) {
        return n -> {
            double result = signal.at(n);
            for (DiscreteSignal s : signals) {
                result = apply(result, s.at(n));
            }
            return result;
        };
    }

    Operator addition = Double::sum;
    Operator subtraction = (value1, value2) -> value1 - value2;
    Operator multiplication = (value1, value2) -> value1 * value2;
    Operator division = (value1, value2) -> value1 / value2;

}
