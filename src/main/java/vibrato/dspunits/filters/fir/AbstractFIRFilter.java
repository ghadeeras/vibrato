package vibrato.dspunits.filters.fir;

import vibrato.dspunits.filters.AbstractLinearFilter;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public abstract class AbstractFIRFilter extends AbstractLinearFilter {

    protected AbstractFIRFilter(RealValue input, int order) {
        super(input, order);
    }

    protected AbstractFIRFilter(RealValue source, AbstractDelayLine state) {
        super(source, state);
    }

    @Override
    protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
        return input;
    }

}
