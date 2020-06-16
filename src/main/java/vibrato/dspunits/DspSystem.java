package vibrato.dspunits;

import vibrato.dspunits.filters.Wire;
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

    protected final DspController<RealValue, RealValue, RealValue> scalarMultiplication = control -> input -> new Wire(() -> control.value() * input.value());

}
