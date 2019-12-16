package vibrato.dspunits.filters.fir;

import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class Delay extends AbstractFIRFilter {

    private final int delay;

    public Delay(RealValue input, int delay) {
        super(input, delay);
        this.delay = delay;
    }

    public Delay(RealValue input, AbstractDelayLine state, int delay) {
        super(input, state);
        this.delay = delay;
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> state.value(-delay);
    }
}
