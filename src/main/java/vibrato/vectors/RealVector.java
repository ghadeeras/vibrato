package vibrato.vectors;

import vibrato.functions.DiscreteRealFunction;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public interface RealVector extends DiscreteRealFunction {

    int size();

    double value(int index);

    default double firstValue() {
        return value(0);
    }

    default double lastValue() {
        return value(size() - 1);
    }

    @Override
    default double apply(int i) {
        return value(i);
    }

    default String contentAsString() {
        return IntStream.range(0, size())
            .mapToDouble(this::value)
            .boxed()
            .map(Object::toString)
            .collect(joining(", "));
    }

    static RealVector join(List<RealValue> values) {
        return new RealVector() {

            @Override
            public int size() {
                return values.size();
            }

            @Override
            public double value(int index) {
                return valueAt(index).value();
            }

            @Override
            public RealValue valueAt(int i) {
                return values.get(i);
            }

        };
    }

    static RealVector window(int size, DiscreteRealFunction function) {
        return function.window(size);
    }

}
