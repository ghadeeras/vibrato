package vibrato.functions;

public interface Signal {

    double at(double time);

    default Signal delay(double delay) {
        return t -> at(t - delay);
    }

    default Signal bias(double bias) {
        return x -> at(x) + bias;
    }

    default Signal compress(double factor) {
        return t -> at(t * factor);
    }

    default Signal stretch(double factor) {
        return t -> at(t / factor);
    }

    default Signal reverse() {
        return t -> at(-t);
    }

    default Signal suppress(double factor) {
        return t -> at(t) / factor;
    }

    default Signal amplify(double factor) {
        return t -> at(t) * factor;
    }

    default Signal invert() {
        return t -> -at(t);
    }

    default Signal flip() {
        return t -> -at(-t);
    }

    default Signal causal() {
        return t -> t >= 0 ? at(t) : 0;
    }

    default Signal periodic(double period) {
        return t -> at(t % period);
    }

    default RealFunction asFunction() {
        return this::at;
    }

    default DiscreteSignal discrete() {
        return this::at;
    }

    default double[] samples(int count, double from, double frequency) {
        return delay(-from).compress(frequency).discrete().samples(count, 0, 1);
    }

}
