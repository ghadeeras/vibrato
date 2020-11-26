package vibrato.dspunits.filters.iir;

import vibrato.dspunits.filters.AbstractLinearFilter;
import vibrato.interpolators.Interpolator;
import vibrato.vectors.RealValue;
import vibrato.vectors.RealVector;

public abstract class AbstractIIRFilter extends AbstractLinearFilter {

    protected AbstractIIRFilter(RealValue input, int order) {
        super(input, order);
    }

    protected AbstractIIRFilter(RealValue input, int order, RealValue delayFactor, Interpolator interpolator) {
        super(input, order, delayFactor, interpolator);
    }

    @Override
    protected RealValue inputPlusFeedbackValue(RealValue input, RealVector state) {
        RealValue feedbackValue = feedbackValue(state);
        return () -> input.value() + feedbackValue.value();
    }

    protected abstract RealValue feedbackValue(RealVector state);

}
