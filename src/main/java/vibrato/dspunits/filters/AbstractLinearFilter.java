package vibrato.dspunits.filters;

import vibrato.dspunits.DspUnit;
import vibrato.dspunits.Wire;
import vibrato.vectors.*;

import static vibrato.oscillators.Oscillator.Operation;

public abstract class AbstractLinearFilter extends DspUnit implements RealValue {

    private final Wire inputPlusFeedback;
    private final Wire output;
    private final Operation stateUpdate;

    public AbstractLinearFilter(RealValue input, int order) {
        this(input, order == 1 ? new DelayUnit() : new CircularBuffer(order));
    }

    public AbstractLinearFilter(RealValue input, AbstractDelayLine state) {
        this.inputPlusFeedback = new Wire(inputPlusFeedbackValue(input, state));
        this.output = new Wire(outputValue(inputPlusFeedback, state));
        this.stateUpdate = state.readingFrom(inputPlusFeedback);
    }

    protected abstract RealValue inputPlusFeedbackValue(RealValue input, RealVector state);

    protected abstract RealValue outputValue(RealValue feedback, RealVector state);

    @Override
    public double value() {
        return output.value();
    }

    @Override
    protected Operation[] operations() {
        return ops(
            ops(inputPlusFeedback),
            ops(output),
            ops(stateUpdate)
        );
    }

}
