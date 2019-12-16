package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspFilter;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class Delay extends AbstractFIRFilter {

    private final int delay;

    private Delay(RealValue input, int delay) {
        super(input, delay);
        this.delay = delay;
    }

    private Delay(RealValue input, AbstractDelayLine state, int delay) {
        super(input, state);
        this.delay = delay;
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> state.value(-delay);
    }

    DspFilter<RealValue, RealValue> of(int delay) {
        return input -> new Delay(input, delay);
    }

}
