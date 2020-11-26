package vibrato.dspunits.filters.fir;

import vibrato.dspunits.DspFilter;
import vibrato.dspunits.filters.AbstractLinearFilter;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public abstract class AbstractFIRFilter extends AbstractLinearFilter {

    protected AbstractFIRFilter(RealValue input, int order) {
        super(input, order);
    }

    @Override
    protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
        return input;
    }

    public static DspFilter<RealValue, RealValue> create(int order, OutputFunction outputFunction) {
        return input -> new AbstractFIRFilter(input, order) {

            @Override
            protected RealValue outputValue(RealValue feedback, RealVector state) {
                return () -> outputFunction.outputValue(feedback.value(), state);
            }

        };
    }

}
