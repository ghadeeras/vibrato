package vibrato.dspunits;

import vibrato.dspunits.filters.Line;
import vibrato.dspunits.filters.Wire;
import vibrato.functions.Linear;
import vibrato.functions.RealFunction;
import vibrato.vectors.Buffer;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DspSystem extends CompositeUnit {

    protected DspSystem(double clockSpeed) {
        super(clockSpeed);
    }

    protected Source<RealValue> scalarConstant(double value) {
        return fromSource(() -> value);
    }

    @SafeVarargs
    protected final Source<RealVector> join(Source<RealValue>... sources) {
        List<RealValue> outputs = Stream.of(sources).map(source -> source.source.output()).collect(toList());
        return from(DspSource.create(RealVector.join(outputs)));
    }

    protected final Filter<RealValue, RealValue> scalarFunction(RealFunction function) {
        return from(input -> new Wire(() -> function.apply(input.value())));
    }

    protected final Filter<RealValue, RealValue> scalarMultiplication(double factor) {
        return scalarFunction(Linear.linear(factor));
    }

    protected final Filter<RealValue, RealValue> scalarSquareRoot = scalarFunction(Math::sqrt);
    protected final DspController<RealValue, RealValue, RealValue> scalarMultiplication = control -> input -> new Wire(() -> control.value() * input.value());
    protected final DspController<RealValue, RealValue, RealValue> scalarDivision = control -> input -> new Wire(() -> input.value() / control.value());

    protected final Filter<RealVector, RealValue> vectorLengthSquared = from(input -> new Wire(input::lengthSquared));
    protected final Filter<RealVector, RealValue> vectorLength =  from(input -> new Wire(input::length));
    protected final DspController<RealVector, RealVector, RealVector> vectorDivision = control -> input -> new Line(input.size(), i -> input.value(i) / control.value(i));

    protected final Filter<RealVector, RealValue> dotProduct(double... components) {
        var vector = new Buffer(components);
        return from(input -> new Wire(() -> vector.dotProduct(input, components.length)));
    }

}
