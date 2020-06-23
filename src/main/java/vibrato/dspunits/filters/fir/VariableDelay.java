package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspController;
import vibrato.interpolators.Interpolator;
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

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> interpolator.value(state, -delay.value());
    }

    public static DspController<RealValue, RealValue, RealValue> ofMax(double maxDelay) {
        return ofMax(maxDelay, Interpolator.linear);
    }

    public static DspController<RealValue, RealValue, RealValue> ofMax(double maxDelay, Interpolator interpolator) {
        return delay -> input -> new VariableDelay(input, (int) Math.ceil(maxDelay), delay, interpolator);
    }

}
