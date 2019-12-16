package vibrato.dspunits.filters.fir;

import vibrato.functions.DiscreteSignal;
import vibrato.functions.Signal;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public class FIRFilter extends AbstractFIRFilter {

    private final double[] impulseResponse;

    public FIRFilter(RealValue input, int order, DiscreteSignal impulseResponse) {
        super(input, order);
        this.impulseResponse = impulseResponse.samples(order + 1, 0, 1);
    }

    public FIRFilter(RealValue input, AbstractDelayLine state, Signal impulseResponse) {
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

}
