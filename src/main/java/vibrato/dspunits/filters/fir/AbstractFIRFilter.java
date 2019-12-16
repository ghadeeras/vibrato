package vibrato.dspunits.filters.fir;

import vibrato.dspunits.filters.AbstractLinearFilter;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public abstract class AbstractFIRFilter extends AbstractLinearFilter {

    public AbstractFIRFilter(RealValue input, int order) {
        super(input, order);
    }

    public AbstractFIRFilter(RealValue input, AbstractDelayLine state) {
        super(input, state);
    }

    @Override
    protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
        return input;
    }

}
