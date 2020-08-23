package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspFilter;
import vibrato.functions.DiscreteSignal;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class FIRFilter extends AbstractFIRFilter {

    private final double[] impulseResponse;

    private FIRFilter(RealValue input, int order, DiscreteSignal impulseResponse) {
        super(input, order);
        this.impulseResponse = impulseResponse.samples(order + 1, 0, 1);
    }

    private FIRFilter(RealValue input, AbstractDelayLine state, DiscreteSignal impulseResponse) {
        super(input, state);
        this.impulseResponse = impulseResponse.samples(state.size() + 1, 0, 1);
    }

    @Override
    protected RealValue outputValue(RealValue feedback, RealVector state) {
        return () -> {
            double value = feedback.value() * impulseResponse[0];
            for (int i = 1; i < impulseResponse.length; i++) {
                value += state.value(-i) * impulseResponse[i];
            }
            return value;
        };
    }

    public static DspFilter<RealValue, RealValue> withImpulseResponse(RealVector impulseResponse) {
        return input -> new FIRFilter(input, impulseResponse.size() - 1, impulseResponse.asSignal());
    }

}
