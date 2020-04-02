package vibrato.dspunits.filters;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.CircularBuffer;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

import static vibrato.dspunits.DspUnit.ops;

public class BufferingWindow implements DspSource<RealVector> {

    private final AbstractDelayLine state;
    private final Operation stateUpdate;

    private BufferingWindow(RealValue input, int size) {
        this.state = new CircularBuffer(size);
        this.stateUpdate = state.readingFrom(input);
    }

    @Override
    public Operation[] operations() {
        return ops(stateUpdate);
    }

    @Override
    public RealVector output() {
        return state;
    }

    public static DspFilter<RealValue, RealVector> ofSize(int size) {
        return input-> new BufferingWindow(input, size);
    }

}
