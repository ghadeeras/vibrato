package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspFilter;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class Delay extends AbstractFIRFilter {

    private final int delay;

    private Delay(RealValue input, int delay) {
        super(input, delay);
        this.delay = delay;
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> state.value(-delay);
    }

    public static DspFilter<RealValue, RealValue> create(int delay) {
        return input -> new Delay(input, delay);
    }

}
