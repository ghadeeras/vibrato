package vibrato.dspunits;

import vibrato.oscillators.Oscillator;

import java.util.stream.Stream;

import static vibrato.oscillators.Oscillator.Operation;

public abstract class DspUnit {

    protected abstract Operation[] operations();

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

    public <U extends DspUnit> void connectTo(Oscillator oscillator) {
        oscillator.triggers(operations());
    }

}
