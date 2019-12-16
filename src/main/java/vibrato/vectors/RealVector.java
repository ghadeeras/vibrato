package vibrato.vectors;

import vibrato.functions.DiscreteRealFunction;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public interface RealVector extends DiscreteRealFunction {

    int size();

    double firstValue();

    double lastValue();

    double value(int index);

    @Override
    default double apply(int i) {
        return value(i);
    }

    default RealValue valueAt(int i) {
        return () -> value(i);
    }

    default String contentAsString() {
        return IntStream.range(0, size())
            .mapToDouble(this::value)
            .boxed()
            .map(Object::toString)
            .collect(joining(", "));
    }

}
