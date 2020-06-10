package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspController;
import vibrato.functions.RealFunction;
import vibrato.interpolators.Interpolator;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class VariableDelay extends AbstractFIRFilter {

    private final RealValue delay;
    private final Interpolator interpolator;

    private VariableDelay(RealValue input, int maxDelay, RealValue delay, Interpolator interpolator) {
        super(input, maxDelay);
        this.delay = delay;
        this.interpolator = interpolator;
    }

    private VariableDelay(RealValue input, AbstractDelayLine state, RealValue delay, Interpolator interpolator) {
        super(input, state);
        this.delay = delay;
        this.interpolator = interpolator;
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        RealFunction interpolatedState = state.interpolated(interpolator);
        return () -> interpolatedState.apply(-delay.value());
    }

    public static DspController<RealValue, RealValue, RealValue> ofMax(int maxDelay) {
        return ofMax(maxDelay, Interpolator.linear);
    }

    public static DspController<RealValue, RealValue, RealValue> ofMax(int maxDelay, Interpolator interpolator) {
        return delay -> input -> new VariableDelay(input, maxDelay, delay, interpolator);
    }

}
