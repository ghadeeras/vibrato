package vibrato.dspunits;

import vibrato.oscillators.Operation;
import vibrato.oscillators.Oscillator;

import java.util.Collection;
import java.util.stream.Stream;

public interface DspUnit {

    Operation[] operations();

    default void connectTo(Oscillator oscillator) {
        for (Operation operation : operations()) {
            oscillator.triggers(operation);
        }
    }

    static Operation[] noOps() {
        return new Operation[0];
    }

    static <D extends DspUnit, C extends Collection<D>> Operation[] ops(C dspUnits) {
        return dspUnits.stream()
            .map(DspUnit::operations)
            .flatMap(Stream::of)
            .toArray(Operation[]::new);
    }

    static Operation[] ops(DspUnit... dspUnits) {
        return Stream.of(dspUnits)
            .map(DspUnit::operations)
            .flatMap(Stream::of)
            .toArray(Operation[]::new);
    }

    static Operation[] ops(Operation[]... operations) {
        return Stream.of(operations)
            .flatMap(Stream::of)
            .toArray(Operation[]::new);
    }

    static Operation[] ops(Operation... operations) {
        return operations;
    }

    static DspUnit create(Operation... operations) {
        return () -> operations;
    }

}
