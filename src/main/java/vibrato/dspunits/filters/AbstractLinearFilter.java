package vibrato.dspunits.filters;

import vibrato.dspunits.DspSource;
import vibrato.oscillators.Operation;
import vibrato.vectors.*;

import static vibrato.dspunits.DspUnit.ops;

public abstract class AbstractLinearFilter implements DspSource<RealValue>, RealValue {

    private final Wire inputPlusFeedback;
    private final Wire output;
    private final Operation stateUpdate;

    protected AbstractLinearFilter(RealValue input, int order) {
        this(input, order == 1 ? new DelayUnit() : new CircularBuffer(order));
    }

    protected AbstractLinearFilter(RealValue input, AbstractDelayLine state) {
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
    public Operation[] operations() {
        return ops(
            ops(inputPlusFeedback),
            ops(output),
            ops(stateUpdate)
        );
    }

    @Override
    public RealValue output() {
        return output;
    }

}
