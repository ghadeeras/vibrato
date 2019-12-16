package vibrato.dspunits.filters.iir;

import vibrato.dspunits.filters.AbstractLinearFilter;
import vibrato.vectors.AbstractDelayLine;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public abstract class AbstractIIRFilter extends AbstractLinearFilter {

    public AbstractIIRFilter(RealValue input, int order) {
        super(input, order);
    }

    public AbstractIIRFilter(RealValue input, AbstractDelayLine state) {
        super(input, state);
    }

    @Override
    protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
        RealValue feedbackValue = feedbackValue(state);
        return () -> input.value() + feedbackValue.value();
    }

    protected abstract RealValue feedbackValue(RealVector state);

}
