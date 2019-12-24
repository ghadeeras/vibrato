package vibrato.dspunits;

import vibrato.oscillators.Operation;

import java.util.stream.Stream;

public abstract class DspUnit {

    public abstract Operation[] operations();

    protected static Operation[] ops(DspUnit... dspUnits) {
        return Stream.of(dspUnits)
            .map(DspUnit::operations)
            .flatMap(Stream::of)
            .toArray(Operation[]::new);
    }

    protected static Operation[] ops(Operation... operations) {
        return operations;
    }

    protected static Operation[] ops(Operation[]... operations) {
        return Stream.of(operations)
            .flatMap(Stream::of)
            .toArray(Operation[]::new);
    }

}
